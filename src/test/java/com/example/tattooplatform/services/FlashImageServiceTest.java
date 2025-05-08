package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.flashimage.FlashImageUrlDto;
import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.repos.FlashImageRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
    void getFlashImagesByCategoryPageable_shouldGetCorrectFlashImage() {
        ImageCategory imageCategory = ImageCategory.builder()
                .category("test")
                .build();

        FlashImage flashImage = FlashImage.builder()
                .name("testImage")
                .categories(List.of(imageCategory))
                .build();
        flashImageRepo.save(flashImage);

        List<FlashImage> foundFlashImages = flashImageService.getFlashImagesByCategoryPaginated(
                imageCategory, PageRequest.of(0, 1)).stream().toList();

        assertFalse(foundFlashImages.isEmpty());
        assertEquals("testImage", foundFlashImages.get(0).getName());
    }

    @Test
    void getFlashImagesByCategoryPageable_shouldNotGetFlashImage_ifItDoesNotHaveCorrectCategory() {
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

        List<FlashImage> foundFlashImages = flashImageService.getFlashImagesByCategoryPaginated(
                imageCategoryOne, PageRequest.of(0, 1)).stream().toList();

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
}
