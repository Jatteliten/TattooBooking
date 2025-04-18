package com.example.demo.services;

import com.example.demo.model.FlashImage;
import com.example.demo.model.ImageCategory;
import com.example.demo.repos.FlashImageRepo;
import org.springframework.stereotype.Service;

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
    public void saveFlashImage(FlashImage flashImage){
        flashImageRepo.save(flashImage);
    }
    public void saveListOfFlashImages(List<FlashImage> flashImages){
        flashImageRepo.saveAll(flashImages);
    }

    public void deleteFlashImage(FlashImage flashImage){
        flashImageRepo.delete(flashImage);
    }
    public List<FlashImage> getAllFlashImages(){
        return flashImageRepo.findAll();
    }
    public List<FlashImage> getImagesByCategory(ImageCategory imageCategory){
        return flashImageRepo.findByCategoriesContaining(imageCategory);
    }

    public Map<ImageCategory, List<FlashImage>> getAllImagesMapByCategory(List<ImageCategory> categories){
        Map<ImageCategory, List<FlashImage>> imagesByCategory = new LinkedHashMap<>();

        for (ImageCategory category : categories) {
            List<FlashImage> images = getImagesByCategory(category);
            if (!images.isEmpty()) {
                imagesByCategory.put(category, images);
            }
        }

        return imagesByCategory;
    }
}
