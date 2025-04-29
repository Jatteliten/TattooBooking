package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dtos.imagecategorydtos.ImageCategoryWithOnlyCategoryDto;
import com.example.tattooPlatform.model.FlashImage;
import com.example.tattooPlatform.model.ImageCategory;
import com.example.tattooPlatform.model.TattooImage;
import com.example.tattooPlatform.repos.ImageCategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<ImageCategory> getAllImageCategories(){
        return imageCategoryRepo.findAll();
    }

    public List<ImageCategory> getCategoriesByIds(List<UUID> ids) {
        return imageCategoryRepo.findAllById(ids);
    }

    public ImageCategory getImageCategoryByCategoryName(String category){
        return imageCategoryRepo.findByCategory(category);
    }

    public List<ImageCategory> getAllImageCategoriesWithFlashImages(){
        return imageCategoryRepo.findAll().stream().filter(category -> !category.getFlashImages().isEmpty()).toList();
    }

    public List<ImageCategory> getAllImageCategoriesWithTattooImages(){
        return imageCategoryRepo.findAll().stream().filter(category -> !category.getTattooImages().isEmpty()).toList();
    }

    public ImageCategoryWithOnlyCategoryDto convertImageCategoryToImageCategoryWithOnlyCategory(ImageCategory imageCategory){
        return ImageCategoryWithOnlyCategoryDto.builder()
                .category(imageCategory.getCategory())
                .build();
    }

    public List<ImageCategoryWithOnlyCategoryDto> convertImageCategoryListToImageCategoryWithOnlyCategoryDtoList(
            List<ImageCategory> imageCategories){
        return imageCategories.stream()
                .map(this::convertImageCategoryToImageCategoryWithOnlyCategory)
                .collect(Collectors.toList());
    }

    public List<ImageCategory> filterImageCategoriesWithoutFlashImages(List<ImageCategory> imageCategories){
        return imageCategories.stream().filter(imageCategory -> !imageCategory.getFlashImages().isEmpty()).toList();
    }

}
