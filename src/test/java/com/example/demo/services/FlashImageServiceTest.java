package com.example.demo.services;

import com.example.demo.dtos.FlashImagedtos.FlashImageOnlyUrlDto;
import com.example.demo.model.FlashImage;
import com.example.demo.model.ImageCategory;
import com.example.demo.repos.FlashImageRepo;
import com.example.demo.repos.ImageCategoryRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class FlashImageServiceTest {
    @Autowired
    FlashImageRepo flashImageRepo;
    @Autowired
    FlashImageService flashImageService;
    @Autowired
    ImageCategoryRepo imageCategoryRepo;

    @AfterEach
    void deleteAll(){
        flashImageRepo.deleteAll();
        imageCategoryRepo.deleteAll();
    }
    @Test
    void saveFlashImage_shouldSaveFlashImage() {
        flashImageService.saveFlashImage(new FlashImage());

        assertEquals(1, flashImageRepo.findAll().size());
    }

    @Test
    void saveListOfFlashImages_shouldSaveFlashImages() {
        flashImageService.saveListOfFlashImages(List.of(new FlashImage(), new FlashImage()));

        assertEquals(2, flashImageRepo.findAll().size());
    }

    @Test
    void deleteFlashImage_shouldDeleteFlashImage() {
        FlashImage flashImage = new FlashImage();
        flashImageRepo.save(flashImage);

        flashImageService.deleteFlashImage(flashImage);

        assertEquals(0, flashImageRepo.findAll().size());
    }

    @Test
    void getFlashImageById_shouldGetCorrectFlashImage() {
        FlashImage flashImage = FlashImage.builder()
                .url("test")
                .build();
        flashImageRepo.save(flashImage);

        FlashImage foundFlashImage = flashImageService.getFlashImageById(flashImage.getId());
        assertEquals(flashImage.getUrl(), foundFlashImage.getUrl());
    }

    @Test
    void getFlashImagesByCategory_shouldGetCorrectFlashImage() {
        ImageCategory imageCategory = ImageCategory.builder()
                .category("test")
                .build();
        imageCategoryRepo.save(imageCategory);
        FlashImage flashImage = FlashImage.builder()
                .name("testImage")
                .categories(List.of(imageCategory))
                .build();
        flashImageRepo.save(flashImage);

        assertFalse(flashImageService.getFlashImagesByCategory(imageCategory).isEmpty());
        assertEquals("testImage",
                flashImageService.getFlashImagesByCategory(imageCategory).get(0).getName());
    }

    @Test
    void getFlashImagesByCategory_shouldNotGetFlashImage_ifItDoesNotHaveCorrectCategory() {
        ImageCategory imageCategoryOne = ImageCategory.builder()
                .category("testOne")
                .build();
        ImageCategory imageCategoryTwo = ImageCategory.builder()
                .category("testTwo")
                .build();
        imageCategoryRepo.saveAll(List.of(imageCategoryOne, imageCategoryTwo));
        FlashImage flashImage = FlashImage.builder()
                .name("testImage")
                .categories(List.of(imageCategoryOne))
                .build();
        flashImageRepo.save(flashImage);

        assertTrue(flashImageService.getFlashImagesByCategory(imageCategoryTwo).isEmpty());
    }

    @Test
    void convertFlashImageToFlashImageOnlyUrlDTO_shouldConvertCorrectly() {
        FlashImage flashImage = FlashImage.builder()
                .url("test")
                .build();

        FlashImageOnlyUrlDto flashImageOnlyUrlDto =
                flashImageService.convertFlashImageToFlashImageOnlyUrlDTO(flashImage);

        assertEquals(flashImage.getUrl(), flashImageOnlyUrlDto.getUrl());
    }

    @Test
    void convertFlashImageListToFlashImagesOnlyUrlDTO_shouldConvertCorrectly() {
        FlashImage flashImageOne = FlashImage.builder()
                .url("test1")
                .build();
        FlashImage flashImageTwo = FlashImage.builder()
                .url("test2")
                .build();

        List<FlashImageOnlyUrlDto> flashImageOnlyUrlDTOs =
                flashImageService.convertFlashImageListToFlashImagesOnlyUrlDTO(
                        List.of(flashImageOne, flashImageTwo));

        assertEquals(flashImageOne.getUrl(), flashImageOnlyUrlDTOs.getFirst().getUrl());
        assertEquals(flashImageTwo.getUrl(), flashImageOnlyUrlDTOs.getLast().getUrl());
    }
}