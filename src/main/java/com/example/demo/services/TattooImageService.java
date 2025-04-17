package com.example.demo.services;

import com.example.demo.model.ImageCategory;
import com.example.demo.model.TattooImage;
import com.example.demo.dtos.tattooImagesdtos.TattooImageUrlDto;
import com.example.demo.repos.TattooImageRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TattooImageService {
    private final TattooImageRepo tattooImageRepo;

    @Value("${image.file.upload.dir}")
    private String imagesDirectory;

    public TattooImageService(TattooImageRepo tattooImageRepo){
        this.tattooImageRepo = tattooImageRepo;
    }

    public List<TattooImage> getAllImages(){
        return tattooImageRepo.findAll();
    }

    public void saveListOfTattooImages(List<TattooImage> tattooImages){
        tattooImageRepo.saveAll(tattooImages);
    }
    public TattooImage findImageByUrl(String url){return tattooImageRepo.findByUrl(url);}

    public TattooImageUrlDto convertTattooImageToTattooImageUrlDto(TattooImage tattooImage){
        return TattooImageUrlDto.builder()
                .url(tattooImage.getUrl())
                .name(tattooImage.getName())
                .build();
    }

    public TattooImage saveImage(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path filePath = Paths.get(imagesDirectory, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return tattooImageRepo.save(TattooImage.builder()
                .name(fileName)
                .contentType(file.getContentType())
                .size(file.getSize())
                .url(filePath.toString())
                .build());
    }

    public TattooImage getImage(UUID id) {
        return tattooImageRepo.findById(id).orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public List<TattooImage> getImageListByCategory(ImageCategory category){
        return tattooImageRepo.findByCategoriesContaining(category);
    }

}
