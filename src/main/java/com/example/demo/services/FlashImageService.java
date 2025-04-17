package com.example.demo.services;

import com.example.demo.model.FlashImage;
import com.example.demo.model.ImageCategory;
import com.example.demo.repos.FlashImageRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashImageService {
    private final FlashImageRepo flashImageRepo;

    public FlashImageService(FlashImageRepo flashImageRepo){
        this.flashImageRepo = flashImageRepo;
    }

    public void saveListOfFlashImages(List<FlashImage> flashImages){
        flashImageRepo.saveAll(flashImages);
    }

    List<FlashImage> getImagesByCategory(ImageCategory imageCategory){
        return flashImageRepo.findByCategoriesContaining(imageCategory);
    }
}
