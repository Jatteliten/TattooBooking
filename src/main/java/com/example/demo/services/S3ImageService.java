package com.example.demo.services;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3ImageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3ImageService(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String s3Key = folder + "/" + fileName;

        Path tempFile = Files.createTempFile("upload-", fileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        try (InputStream inputStream = Files.newInputStream(tempFile)) {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(inputStream, Files.size(tempFile)));
        }

        return "https://" + bucketName + ".s3.eu-north-1.amazonaws.com/" + s3Key;
    }


    public void deleteImage(String imageUrl) {
        String key = imageUrl.substring(imageUrl.indexOf(".com/") + 5);

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
                    String key = url.substring(url.indexOf(".com/") + 5);
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
