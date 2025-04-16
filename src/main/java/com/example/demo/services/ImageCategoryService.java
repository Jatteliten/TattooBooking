package com.example.demo.services;

import com.example.demo.model.ImageCategory;
import com.example.demo.repos.ImageCategoryRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageCategoryService {

    private final ImageCategoryRepo imageCategoryRepo;

    public ImageCategoryService(ImageCategoryRepo imageCategoryRepo){
        this.imageCategoryRepo = imageCategoryRepo;
    }

    public List<ImageCategory> getAllImageCategories(){
        return imageCategoryRepo.findAll();
    }

    public ImageCategory findImageCategoryByCategoryString(String category){
        return imageCategoryRepo.findByCategory(category);
    }
}
