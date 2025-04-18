package com.example.demo.services;

import com.example.demo.model.FlashImage;
import com.example.demo.model.ImageCategory;
import com.example.demo.model.TattooImage;
import com.example.demo.repos.ImageCategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ImageCategoryService {

    private final ImageCategoryRepo imageCategoryRepo;
    private final TattooImageService tattooImageService;
    private final FlashImageService flashImageService;

    public ImageCategoryService(ImageCategoryRepo imageCategoryRepo, TattooImageService tattooImageService, FlashImageService flashImageService){
        this.imageCategoryRepo = imageCategoryRepo;
        this.tattooImageService = tattooImageService;
        this.flashImageService = flashImageService;
    }

    public void saveImageCategory(ImageCategory imageCategory){
        imageCategoryRepo.save(imageCategory);
    }

    public List<ImageCategory> getAllImageCategories(){
        return imageCategoryRepo.findAll();
    }

    public List<ImageCategory> getCategoriesByIds(List<UUID> ids) {
        return imageCategoryRepo.findAllById(ids);
    }

    @Transactional
    public void deleteImageCategory(ImageCategory imageCategory){
        List<TattooImage> tattooImages = imageCategory.getTattooImages();
        List<FlashImage> flashImages = imageCategory.getFlashImages();

        if (tattooImages != null) {
            for (TattooImage tattooImage : tattooImages) {
                tattooImage.getCategories().remove(imageCategory);
            }
            tattooImageService.saveListOfTattooImages(tattooImages);
        }

        if (flashImages != null) {
            for (FlashImage flashImage : flashImages) {
                flashImage.getCategories().remove(imageCategory);
            }
            flashImageService.saveListOfFlashImages(flashImages);
        }

        imageCategoryRepo.flush();

        imageCategory.setTattooImages(null);
        imageCategory.setFlashImages(null);

        imageCategoryRepo.delete(imageCategory);
    }


    public ImageCategory findImageCategoryByCategoryString(String category){
        return imageCategoryRepo.findByCategory(category);
    }
}
