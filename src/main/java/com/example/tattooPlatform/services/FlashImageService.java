package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dto.flashimage.FlashImageUrlDto;
import com.example.tattooPlatform.model.FlashImage;
import com.example.tattooPlatform.model.ImageCategory;
import com.example.tattooPlatform.repos.FlashImageRepo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "flashImages")
public class FlashImageService {
    private final FlashImageRepo flashImageRepo;

    public FlashImageService(FlashImageRepo flashImageRepo){
        this.flashImageRepo = flashImageRepo;
    }

    @CacheEvict(allEntries = true)
    public void saveFlashImage(FlashImage flashImage){
        flashImageRepo.save(flashImage);
    }

    @CacheEvict(allEntries = true)
    public void saveListOfFlashImages(List<FlashImage> flashImages){
        flashImageRepo.saveAll(flashImages);
    }

    @CacheEvict(allEntries = true)
    public void deleteFlashImage(FlashImage flashImage){
        flashImageRepo.delete(flashImage);
    }

    @Cacheable
    public FlashImage getFlashImageById(UUID id){
        return flashImageRepo.findById(id).orElse(null);
    }

    @Cacheable
    public List<FlashImage> getFlashImagesByCategory(ImageCategory imageCategory){
        return flashImageRepo.findByCategoriesContaining(imageCategory);
    }

    public FlashImageUrlDto convertFlashImageToFlashImageUrlDto(FlashImage flashImage){
        return FlashImageUrlDto.builder()
                .url(flashImage.getUrl())
                .build();
    }

    public List<FlashImageUrlDto> convertFlashImageListToFlashImagesUrlDto(List<FlashImage> flashImages){
        return flashImages.stream()
                .map(this::convertFlashImageToFlashImageUrlDto)
                .collect(Collectors.toList());
    }

}
