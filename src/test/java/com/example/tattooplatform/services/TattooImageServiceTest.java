package com.example.tattooplatform.services;

import com.example.tattooplatform.model.Booking;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.model.TattooImage;
import com.example.tattooplatform.repos.BookingRepo;
import com.example.tattooplatform.repos.TattooImageRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    BookingRepo bookingRepo;

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

    @Test
    void getPageOrderedByLatestBookingDate_shouldReturnCorrectItems() {
        ImageCategory imageCategory = ImageCategory.builder().build();

        Booking bookingOne = Booking.builder()
                .date(LocalDateTime.now().minusDays(1))
                .build();
        Booking bookingTwo = Booking.builder()
                .date(LocalDateTime.now())
                .build();

        bookingRepo.saveAll(List.of(bookingOne, bookingTwo));

        TattooImage tattooImageOne = TattooImage.builder()
                .booking(bookingOne)
                .categories(List.of(imageCategory))
                .build();
        TattooImage tattooImageTwo = TattooImage.builder()
                .booking(bookingTwo)
                .categories(List.of(imageCategory))
                .build();

        tattooImageRepo.saveAll(List.of(tattooImageOne, tattooImageTwo));

        Page<TattooImage> pageOne = tattooImageService.getPageOrderedByLatestBookingDate(imageCategory, 0, 1);
        Page<TattooImage> pageTwo = tattooImageService.getPageOrderedByLatestBookingDate(imageCategory, 1, 1);

        assertEquals(1, pageOne.getContent().size());
        assertEquals(tattooImageTwo.getBooking().getDate(), pageOne.getContent().get(0).getBooking().getDate());
        assertEquals(1, pageTwo.getContent().size());
        assertEquals(tattooImageOne.getBooking().getDate(), pageTwo.getContent().get(0).getBooking().getDate());
    }

    @Test
    void countTattooImagesByImageCategory_shouldCountCorrectly(){
        ImageCategory imageCategory = new ImageCategory();
        List<TattooImage> tattooImages = new ArrayList<>();
        int i = 0;

        while (i < 5){
            tattooImages.add(TattooImage.builder().categories(List.of(imageCategory)).build());
            i++;
        }

        tattooImageRepo.saveAll(tattooImages);

        assertEquals(i, tattooImageService.countTattooImagesByImageCategory(imageCategory));
    }

    @Test
    void countTattooImagesByImageCategory_shouldExclude_categoriesThatAreNotGiven(){
        ImageCategory imageCategory = new ImageCategory();
        tattooImageRepo.save(TattooImage.builder().categories(List.of(imageCategory)).build());
        tattooImageRepo.save(TattooImage.builder().categories(List.of(new ImageCategory())).build());

        assertEquals(1, tattooImageService.countTattooImagesByImageCategory(imageCategory));
    }

}