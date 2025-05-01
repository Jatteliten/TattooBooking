package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dto.imagecategory.ImageCategoryDto;
import com.example.tattooPlatform.model.FlashImage;
import com.example.tattooPlatform.model.ImageCategory;
import com.example.tattooPlatform.model.TattooImage;
import com.example.tattooPlatform.repos.ImageCategoryRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class ImageCategoryServiceTest {
    @Autowired
    ImageCategoryRepo imageCategoryRepo;
    @Autowired
    ImageCategoryService imageCategoryService;
    @MockBean
    private TattooImageService tattooImageService;
    @MockBean
    private FlashImageService flashImageService;

    @AfterEach
    void deleteAll(){
        imageCategoryRepo.deleteAll();
    }

    @Test
    void saveImageCategory_shouldSaveImageCategory() {
        int current = imageCategoryRepo.findAll().size();
        imageCategoryService.saveImageCategory(new ImageCategory());

        assertEquals(current + 1, imageCategoryRepo.findAll().size());
    }

    @Test
    void deleteImageCategory_shouldDeleteImageCategory() {
        ImageCategory imageCategory = new ImageCategory();

        imageCategoryRepo.save(imageCategory);
        imageCategoryService.deleteImageCategory(imageCategory);

        assertEquals(0, imageCategoryRepo.findAll().size());
    }

    @Test
    void deleteImageCategory_shouldRemoveImageCategory_fromTattooImageList() {
        ImageCategory imageCategory = new ImageCategory();
        TattooImage tattooImage = new TattooImage();
        tattooImage.setCategories(new ArrayList<>(List.of(imageCategory)));
        imageCategory.setTattooImages(List.of(tattooImage));

        imageCategoryRepo.save(imageCategory);
        imageCategoryService.deleteImageCategory(imageCategory);

        assertFalse(tattooImage.getCategories().contains(imageCategory));
        verify(tattooImageService).saveListOfTattooImages(List.of(tattooImage));
    }

    @Test
    void deleteImageCategory_shouldRemoveImageCategory_fromFlashImageList() {
        ImageCategory imageCategory = new ImageCategory();
        FlashImage flashImage = new FlashImage();
        flashImage.setCategories(new ArrayList<>(List.of(imageCategory)));
        imageCategory.setFlashImages(List.of(flashImage));

        imageCategoryRepo.save(imageCategory);
        imageCategoryService.deleteImageCategory(imageCategory);

        assertFalse(flashImage.getCategories().contains(imageCategory));
        verify(flashImageService).saveListOfFlashImages(List.of(flashImage));
    }

    @Test
    void deleteImageCategory_shouldSetFlashImageAndTattooImages_toNull() {
        ImageCategory imageCategory = new ImageCategory();
        FlashImage flashImage = new FlashImage();
        flashImage.setCategories(new ArrayList<>(List.of(imageCategory)));
        imageCategory.setFlashImages(List.of(flashImage));
        TattooImage tattooImage = new TattooImage();
        tattooImage.setCategories(new ArrayList<>(List.of(imageCategory)));
        imageCategory.setTattooImages(List.of(tattooImage));

        imageCategoryRepo.save(imageCategory);
        imageCategoryService.deleteImageCategory(imageCategory);

        assertNull(imageCategory.getFlashImages());
        assertNull(imageCategory.getTattooImages());
    }

    @Test
    void getAllImageCategories_shouldGetAllImageCategories() {
        int i = 0;
        List<ImageCategory> imageCategories = new ArrayList<>();
        while(i < 5){
            imageCategories.add(new ImageCategory());
            i++;
        }
        imageCategoryRepo.saveAll(imageCategories);

        assertEquals(i, imageCategoryService.getAllImageCategories().size());
    }

    @Test
    void getCategoriesByIds_shouldGetAllImageCategories_byIds() {
        ImageCategory imageCategoryOne = ImageCategory.builder().build();
        ImageCategory imageCategoryTwo = ImageCategory.builder().build();
        List<ImageCategory> imageCategories = List.of(imageCategoryOne, imageCategoryTwo);
        imageCategoryRepo.saveAll(imageCategories);
        List<UUID> imageCategoryIds = List.of(imageCategoryOne.getId(), imageCategoryTwo.getId());

        List<ImageCategory> foundImageCategories = imageCategoryService.getCategoriesByIds(imageCategoryIds);

        assertEquals(2, foundImageCategories.size());
        for(ImageCategory imageCategory: foundImageCategories){
            assertTrue(imageCategoryIds.contains(imageCategory.getId()));
        }
    }

    @Test
    void getImageCategoryByCategoryName_shouldGetImageCategoryByName() {
        ImageCategory imageCategory = ImageCategory.builder()
                .category("test")
                .build();
        imageCategoryRepo.save(imageCategory);

        assertEquals(imageCategory.getId(),
                imageCategoryService.getImageCategoryByCategoryName(imageCategory.getCategory()).getId());
    }

    @Test
    @Transactional
    void getAllImageCategoriesWithFlashImages_shouldReturnImageCategories_withFlashImages() {
        ImageCategory imageCategory = ImageCategory.builder()
                .flashImages(List.of(new FlashImage()))
                .build();
        imageCategoryRepo.save(imageCategory);

        assertTrue(imageCategoryService.getAllImageCategoriesWithFlashImages().contains(imageCategory));
    }

    @Test
    @Transactional
    void getAllImageCategoriesWithFlashImages_shouldNotReturnImageCategories_withoutFlashImages() {
        ImageCategory imageCategory = ImageCategory.builder()
                .flashImages(List.of())
                .build();
        imageCategoryRepo.save(imageCategory);

        assertFalse(imageCategoryService.getAllImageCategoriesWithFlashImages().contains(imageCategory));
    }

    @Test
    @Transactional
    void getAllImageCategoriesWithTattooImages_shouldReturnImageCategories_withTattooImages() {
        ImageCategory imageCategory = ImageCategory.builder()
                .tattooImages(List.of(new TattooImage()))
                .build();
        imageCategoryRepo.save(imageCategory);

        assertTrue(imageCategoryService.getAllImageCategoriesWithTattooImages().contains(imageCategory));
    }

    @Test
    @Transactional
    void getAllImageCategoriesWithTattooImages_shouldNotReturnImageCategories_withoutTattooImages() {
        ImageCategory imageCategory = ImageCategory.builder()
                .tattooImages(List.of())
                .build();
        imageCategoryRepo.save(imageCategory);

        assertFalse(imageCategoryService.getAllImageCategoriesWithTattooImages().contains(imageCategory));
    }

    @Test
    void convertImageCategoryToImageCategoryDto_shouldConvertCorrectly() {
        ImageCategory imageCategory = ImageCategory.builder()
                .category("test")
                .build();

        assertEquals(imageCategory.getCategory(),
                imageCategoryService.convertImageCategoryToImageCategoryDto(imageCategory).getCategory());
    }

    @Test
    void convertImageCategoryListToImageCategoryDtoList_shouldConvertCorrectly() {
        ImageCategory imageCategoryOne = ImageCategory.builder()
                .category("test1")
                .build();
        ImageCategory imageCategoryTwo = ImageCategory.builder()
                .category("test2")
                .build();
        List<ImageCategory> imageCategories = List.of(imageCategoryOne, imageCategoryTwo);

        List<ImageCategoryDto> convertedList =
                imageCategoryService.convertImageCategoryListToImageCategoryDtoList(imageCategories);

        assertEquals(imageCategoryOne.getCategory(), convertedList.getFirst().getCategory());
        assertEquals(imageCategoryTwo.getCategory(), convertedList.getLast().getCategory());
    }

    @Test
    void filterImageCategoriesWithoutFlashImages_shouldPreserveImageCategories_withFlashImages() {
        ImageCategory imageCategory = ImageCategory.builder()
                .flashImages(List.of(new FlashImage()))
                .build();

        assertTrue(imageCategoryService.filterImageCategoriesWithoutFlashImages(
                List.of(imageCategory)).contains(imageCategory));
    }

    @Test
    void filterImageCategoriesWithoutFlashImages_shouldRemoveImageCategories_withoutFlashImages() {
        ImageCategory imageCategory = ImageCategory.builder()
                .flashImages(List.of())
                .build();

        assertFalse(imageCategoryService.filterImageCategoriesWithoutFlashImages(
                List.of(imageCategory)).contains(imageCategory));
    }
}