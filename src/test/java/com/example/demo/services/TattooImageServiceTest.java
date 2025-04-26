package com.example.demo.services;

import com.example.demo.model.TattooImage;
import com.example.demo.repos.TattooImageRepo;
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
class TattooImageServiceTest {
    @Autowired
    TattooImageRepo tattooImageRepo;
    @Autowired
    TattooImageService tattooImageService;

    @AfterEach
    void deleteAll(){
        tattooImageRepo.deleteAll();
    }

    @Test
    void saveTattooImage_shouldSaveTattooImage() {
        tattooImageService.saveTattooImage(new TattooImage());

        assertEquals(1, tattooImageRepo.findAll().size());
    }

    @Test
    void saveListOfTattooImages_shouldSaveListOfTattooImages() {
        tattooImageService.saveListOfTattooImages(List.of(new TattooImage(), new TattooImage()));

        assertEquals(2, tattooImageRepo.findAll().size());
    }

    @Test
    void deleteTattooImage_shouldDeleteTattooImage() {
        TattooImage tattooImage = new TattooImage();
        tattooImageRepo.save(tattooImage);

        assertFalse(tattooImageRepo.findAll().isEmpty());

        tattooImageService.deleteTattooImage(tattooImage);

        assertTrue(tattooImageRepo.findAll().isEmpty());
    }

    @Test
    void deleteListOfTattooImages_shouldDeleteListOfTattooImages() {
        List<TattooImage> tattooImages = List.of(new TattooImage(), new TattooImage());
        tattooImageRepo.saveAll(tattooImages);

        assertFalse(tattooImageRepo.findAll().isEmpty());

        tattooImageService.deleteListOfTattooImages(tattooImages);

        assertTrue(tattooImageRepo.findAll().isEmpty());
    }

}