package com.example.demo.services;

import com.example.demo.model.FlashImage;
import com.example.demo.model.ImageCategory;
import com.example.demo.repos.FlashImageRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FlashImageService {
    private final FlashImageRepo flashImageRepo;

    public FlashImageService(FlashImageRepo flashImageRepo){
        this.flashImageRepo = flashImageRepo;
    }

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

    public List<FlashImage> getAllFlashImages(){
        return flashImageRepo.findAll();
    }

    public List<FlashImage> getFlashImagesByCategory(ImageCategory imageCategory){
        return flashImageRepo.findByCategoriesContaining(imageCategory);
    }

    @Cacheable("flashImages")
    public Map<ImageCategory, ArrayList<FlashImage>> getAllFlashImagesMapByCategory(){
        Map<ImageCategory, ArrayList<FlashImage>> imagesByCategory = new LinkedHashMap<>();
        List<FlashImage> flashImages = getAllFlashImages();

        for (FlashImage flashImage: flashImages) {
            ImageCategory flashImageCategory = flashImage.getCategories().getFirst();
            if (imagesByCategory.get(flashImageCategory) == null) {
                imagesByCategory.put(flashImageCategory, new ArrayList<>(List.of(flashImage)));
            }else{
                imagesByCategory.get(flashImageCategory).add(flashImage);
            }
        }

        return imagesByCategory;
    }
}
