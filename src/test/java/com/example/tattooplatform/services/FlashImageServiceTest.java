package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.flashimage.FlashImageUrlDto;
import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.repos.FlashImageRepo;
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

    @AfterEach
    void deleteAll(){
        flashImageRepo.deleteAll();
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

        FlashImage flashImage = FlashImage.builder()
                .name("testImage")
                .categories(List.of(imageCategory))
                .build();
        flashImageRepo.save(flashImage);

        List<FlashImage> result = flashImageService.getFlashImagesByCategory(imageCategory);

        assertFalse(result.isEmpty());
        assertEquals("testImage", result.get(0).getName());
    }

    @Test
    void getFlashImagesByCategory_shouldNotGetFlashImage_ifItDoesNotHaveCorrectCategory() {
        ImageCategory imageCategoryOne = ImageCategory.builder()
                .category("testOne")
                .build();
        ImageCategory imageCategoryTwo = ImageCategory.builder()
                .category("testTwo")
                .build();

        FlashImage flashImageOne = FlashImage.builder()
                .name("testImageOne")
                .categories(List.of(imageCategoryOne))
                .build();
        FlashImage flashImageTwo = FlashImage.builder()
                .name("testImageTwo")
                .categories(List.of(imageCategoryTwo))
                .build();
        flashImageRepo.saveAll(List.of(flashImageOne, flashImageTwo));

        List<FlashImage> foundFlashImages = flashImageService.getFlashImagesByCategory(imageCategoryOne);

        assertEquals(foundFlashImages.getFirst().getName(), flashImageOne.getName());
        assertEquals(1, foundFlashImages.size());
    }

    @Test
    void convertFlashImageToFlashImageUrlDto_shouldConvertCorrectly() {
        FlashImage flashImage = FlashImage.builder()
                .url("test")
                .build();

        FlashImageUrlDto dto = flashImageService.convertFlashImageToFlashImageUrlDto(flashImage);

        assertEquals(flashImage.getUrl(), dto.getUrl());
    }

    @Test
    void convertFlashImageListToFlashImagesUrlDto_shouldConvertCorrectly() {
        FlashImage flashImageOne = FlashImage.builder()
                .url("test1")
                .build();
        FlashImage flashImageTwo = FlashImage.builder()
                .url("test2")
                .build();

        List<FlashImageUrlDto> dtos = flashImageService.convertFlashImageListToFlashImagesUrlDto(
                List.of(flashImageOne, flashImageTwo));

        assertEquals(flashImageOne.getUrl(), dtos.get(0).getUrl());
        assertEquals(flashImageTwo.getUrl(), dtos.get(1).getUrl());
    }
}
