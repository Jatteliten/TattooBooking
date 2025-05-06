package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.imagecategory.ImageCategoryDto;
import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.model.TattooImage;
import com.example.tattooplatform.repos.ImageCategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public List<ImageCategory> filterAllImageCategoriesWithTattooImages(){
        return imageCategoryRepo.findAll().stream().filter(category -> !category.getTattooImages().isEmpty()).toList();
    }

    public ImageCategoryDto convertImageCategoryToImageCategoryDto(ImageCategory imageCategory){
        return ImageCategoryDto.builder()
                .category(imageCategory.getCategory())
                .build();
    }

    public List<ImageCategoryDto> convertImageCategoryListToImageCategoryDtoList(
            List<ImageCategory> imageCategories){
        return imageCategories.stream()
                .map(this::convertImageCategoryToImageCategoryDto)
                .toList();
    }

    public List<ImageCategory> filterImageCategoriesWithoutFlashImages(List<ImageCategory> imageCategories){
        return imageCategories.stream().filter(imageCategory -> !imageCategory.getFlashImages().isEmpty()).toList();
    }

    public List<ImageCategory> sortImageCategoriesByName(List<ImageCategory> imageCategories){
        return imageCategories.stream()
                .sorted(Comparator.comparing(ImageCategory::getCategory))
                .toList();
    }

}
