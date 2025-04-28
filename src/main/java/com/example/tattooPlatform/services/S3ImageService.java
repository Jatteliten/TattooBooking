package com.example.tattooPlatform.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;


@Service
public class S3ImageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String cloudFrontUrl;

    public S3ImageService(S3Client s3Client,
                          @Value("${aws.s3.bucket-name}") String bucketName,
                          @Value("${aws.cloudfront.url}") String cloudFrontUrl) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.cloudFrontUrl = cloudFrontUrl;
    }


    public String uploadImage(MultipartFile file, String folder) throws IOException {
        ByteArrayInputStream resizedImage;

        if (file.getSize() > 1048576) {
            System.out.println("hej");
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            int newWidth = originalWidth / 2;
            int newHeight = originalHeight / 2;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .size(newWidth, newHeight)
                    .outputFormat("jpeg")
                    .toOutputStream(outputStream);

            resizedImage = new ByteArrayInputStream(outputStream.toByteArray());
        } else {
            resizedImage = new ByteArrayInputStream(file.getBytes());
        }

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String s3Key = folder + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .cacheControl("public, max-age=31536000")
                .build();

        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(resizedImage, resizedImage.available()));
        } catch (Exception e) {
            throw new IOException("Error uploading image", e);
        }
        System.out.println(cloudFrontUrl + "/" + s3Key);

        return cloudFrontUrl + "/" + s3Key;
    }
    public void deleteImage(String imageUrl) {
        String key = imageUrl.replace(cloudFrontUrl + "/", "");

        String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(decodedKey)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public void deleteImages(List<String> imageUrls) {
        List<ObjectIdentifier> objectsToDelete = imageUrls.stream()
                .map(url -> {
                    String key = url.replace(cloudFrontUrl + "/", "");
                    String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
                    return ObjectIdentifier.builder().key(decodedKey).build();
                })
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objectsToDelete).build())
                .build();

        s3Client.deleteObjects(deleteRequest);
    }

}