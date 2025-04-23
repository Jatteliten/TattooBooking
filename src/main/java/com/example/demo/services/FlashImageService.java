package com.example.demo.services;

import com.example.demo.dtos.FlashImagedtos.FlashImageOnlyUrlDto;
import com.example.demo.model.FlashImage;
import com.example.demo.model.ImageCategory;
import com.example.demo.repos.FlashImageRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FlashImageService {
    private final FlashImageRepo flashImageRepo;

    public FlashImageService(FlashImageRepo flashImageRepo){
        this.flashImageRepo = flashImageRepo;
    }

    @Cacheable(value = "flashImagesById", key = "#id")
    public FlashImage getFlashImageById(UUID id){
        return flashImageRepo.findById(id).orElse(null);
    }
    @CacheEvict(value = "flashImages", allEntries = true)
    public void saveFlashImage(FlashImage flashImage){
        flashImageRepo.save(flashImage);
    }

    @CacheEvict(value = "flashImages", allEntries = true)
    public void saveListOfFlashImages(List<FlashImage> flashImages){
        flashImageRepo.saveAll(flashImages);
    }

    @CacheEvict(value = "flashImages", allEntries = true)
    public void deleteFlashImage(FlashImage flashImage){
        flashImageRepo.delete(flashImage);
    }

    @Cacheable(value = "flashImagesByCategory", key = "#imageCategory.id")
    public List<FlashImage> getFlashImagesByCategory(ImageCategory imageCategory){
        return flashImageRepo.findByCategoriesContaining(imageCategory);
    }

    public FlashImageOnlyUrlDto convertFlashImageToFlashImageOnlyUrlDTO(FlashImage flashImage){
        return FlashImageOnlyUrlDto.builder()
                .url(flashImage.getUrl())
                .build();
    }

    public List<FlashImageOnlyUrlDto> convertFlashImageListToFlashImagesOnlyUrlDTO(List<FlashImage> flashImages){
        return flashImages.stream()
                .map(this::convertFlashImageToFlashImageOnlyUrlDTO)
                .collect(Collectors.toList());
    }

}
