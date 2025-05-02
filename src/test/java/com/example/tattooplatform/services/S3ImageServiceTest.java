package com.example.tattooplatform.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class S3ImageServiceTest {

    @Mock
    private S3Client s3Client;

    private S3ImageService s3ImageService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String REGION_NAME = "test-region";
    private static final String CLOUD_FRONT_URL = "test-cloudfront";
    private static final String TEST_FOLDER = "test-folder";
    private static final String TEST_IMAGE_NAME = "test-image.jpg";
    private static final MockMultipartFile TEST_FILE = new MockMultipartFile(
            "file",
            TEST_IMAGE_NAME,
            "image/jpeg",
            "test image content".getBytes()
    );

    @BeforeEach
    void setUp() {
        System.setProperty("java.awt.headless", "true");
        MockitoAnnotations.openMocks(this);
        s3ImageService = new S3ImageService(s3Client, BUCKET_NAME, CLOUD_FRONT_URL);
    }

    @Test
    void uploadImage_shouldSetCorrectFolderName() throws IOException {
        String imageUrl = s3ImageService.uploadImage(TEST_FILE, TEST_FOLDER);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertThat(imageUrl).contains(TEST_FOLDER);
    }

    @Test
    void uploadImage_shouldSetCorrectCloudFrontLink() throws IOException {
        String imageUrl = s3ImageService.uploadImage(TEST_FILE, TEST_FOLDER);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertThat(imageUrl).contains(CLOUD_FRONT_URL);
    }

    @Test
    void uploadImage_shouldCallResizeImage_whenFileIsLarge() throws IOException, URISyntaxException {
        Path path = Paths.get(
                Objects.requireNonNull(getClass().getClassLoader().getResource("images/large-file.jpg")).toURI());
        byte[] imageBytes = Files.readAllBytes(path);

        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "your-test-image.jpg",
                "image/jpeg",
                imageBytes
        );

        S3ImageService spyS3ImageService = spy(s3ImageService);

        spyS3ImageService.uploadImage(largeFile, TEST_FOLDER);

        verify(spyS3ImageService, times(1)).resizeImage(largeFile);
    }

    @Test
    void uploadImage_shouldNotCallResizeImage_whenFileIsSmall() throws IOException, URISyntaxException {
        Path path = Paths.get(
                Objects.requireNonNull(getClass().getClassLoader().getResource("images/small-file.jpg")).toURI());
        byte[] imageBytes = Files.readAllBytes(path);

        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "your-test-image.jpg",
                "image/jpeg",
                imageBytes
        );

        S3ImageService spyS3ImageService = spy(s3ImageService);

        spyS3ImageService.uploadImage(largeFile, TEST_FOLDER);

        verify(spyS3ImageService, times(0)).resizeImage(largeFile);
    }

    @Test
    void resizeImage_shouldResizeImageCorrectly() throws IOException, URISyntaxException {
        Path path = Paths.get(
                Objects.requireNonNull(getClass().getClassLoader().getResource("images/large-file.jpg")).toURI());
        byte[] imageBytes = Files.readAllBytes(path);

        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "your-test-image.jpg",
                "image/jpeg",
                imageBytes
        );

        BufferedImage originalImage = ImageIO.read(largeFile.getInputStream());

        ByteArrayInputStream resizedImage = s3ImageService.resizeImage(largeFile);

        BufferedImage resizedBufferedImage = ImageIO.read(resizedImage);
        assertThat(resizedBufferedImage.getWidth()).isEqualTo(originalImage.getWidth() / 2);
        assertThat(resizedBufferedImage.getHeight()).isEqualTo(originalImage.getHeight() / 2);
    }

    @Test
    void deleteImage_deleteRequest_shouldContainCorrectValues() {
        String imageUrl = "https://test-bucket.s3." + REGION_NAME + ".amazonaws.com/test-folder/" + TEST_IMAGE_NAME;

        s3ImageService.deleteImage(imageUrl);

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(captor.capture());

        DeleteObjectRequest deleteRequest = captor.getValue();
        assertThat(deleteRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(deleteRequest.key()).contains(TEST_FOLDER);
        assertThat(deleteRequest.key()).contains(TEST_IMAGE_NAME);
    }

    @Test
    void deleteImages_deleteRequest_shouldContainCorrectValues() {
        List<String> imageUrls = List.of(
                "https://test-bucket.s3." + REGION_NAME + ".amazonaws.com/test-folder/image1.jpg",
                "https://test-bucket.s3." + REGION_NAME + ".amazonaws.com/test-folder/image2.jpg"
        );

        s3ImageService.deleteImages(imageUrls);

        ArgumentCaptor<DeleteObjectsRequest> captor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(s3Client, times(1)).deleteObjects(captor.capture());

        DeleteObjectsRequest deleteRequest = captor.getValue();
        assertThat(deleteRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(deleteRequest.delete().objects()).hasSize(2);
    }
}