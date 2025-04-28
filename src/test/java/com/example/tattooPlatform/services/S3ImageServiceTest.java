package com.example.tattooPlatform.services;

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

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class S3ImageServiceTest {

    @Mock
    private S3Client s3Client;

    private S3ImageService s3ImageService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String REGION_NAME = "test-region";
    private static final String CLOUD_FRONT_URL = "test-cloudfront";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        s3ImageService = new S3ImageService(s3Client, BUCKET_NAME, CLOUD_FRONT_URL);
    }

    @Test
    void testUploadImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String imageUrl = s3ImageService.uploadImage(file, "test-folder");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertThat(imageUrl).contains(BUCKET_NAME);
        assertThat(imageUrl).contains("test-folder");
    }

    @Test
    void testDeleteImage() {
        String imageUrl = "https://test-bucket.s3." + REGION_NAME + ".amazonaws.com/test-folder/test-image.jpg";

        s3ImageService.deleteImage(imageUrl);

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(captor.capture());

        DeleteObjectRequest deleteRequest = captor.getValue();
        assertThat(deleteRequest.bucket()).isEqualTo(BUCKET_NAME);
        assertThat(deleteRequest.key()).isEqualTo("test-folder/test-image.jpg");
    }

    @Test
    void testDeleteImages() {
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