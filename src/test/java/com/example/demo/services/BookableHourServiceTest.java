package com.example.demo.services;

import com.example.demo.dtos.bokablehourdtos.BookableHourForCalendarDto;
import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.repos.BookableHourRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class BookableHourServiceTest {
    @Autowired
    BookableHourRepo bookableHourRepo;
    @Autowired
    BookableHourService bookableHourService;

    @Test
    void saveAllBookableHours_shouldSaveAllBookableHours(){
        BookableHour bookableHourOne = new BookableHour();
        BookableHour bookableHourTwo = new BookableHour();
        BookableHour bookableHourThree = new BookableHour();

        bookableHourService.saveAllBookableHours(List.of(bookableHourOne, bookableHourTwo, bookableHourThree));

        assertEquals(3, bookableHourRepo.findAll().size());
        bookableHourRepo.deleteAll();
    }

    @Test
    void setBookableHoursInBookableDateToBookedBetweenStartAndEndTime_shouldSetBookableHoursBetweenHoursToBooked(){
        LocalTime now = LocalTime.now();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(now.plusHours(1))
                .booked(false)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(now.plusHours(2))
                .booked(false)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableHourService.setBookableHoursInBookableDateToBookedBetweenStartAndEndTime(
                bookableDate, now, now.plusHours(3));

        assertTrue(bookableHourOne.isBooked());
        assertTrue(bookableHourTwo.isBooked());
    }

    @Test
    void setBookableHoursInBookableDateToBookedBetweenStartAndEndTime_shouldNotSetBookableHoursOutsideHoursRangeToBooked(){
        LocalTime now = LocalTime.now();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(now)
                .booked(false)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(now.plusHours(1))
                .booked(false)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableHourService.setBookableHoursInBookableDateToBookedBetweenStartAndEndTime(
                bookableDate, now.plusHours(2), now.plusHours(3));

        assertFalse(bookableHourOne.isBooked());
        assertFalse(bookableHourTwo.isBooked());
    }

    @Test
    void convertBookableHourToBookableHourForCalendarDto_shouldConvertAttributesCorrectly(){
        LocalTime now = LocalTime.now();
        LocalTime inOneHour = LocalTime.now().plusHours(1);
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(now)
                .booked(false)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(inOneHour)
                .booked(true)
                .build();

        BookableHourForCalendarDto bookableHourForCalendarDtoOne =
                bookableHourService.convertBookableHourToBookableHourForCalendarDto(bookableHourOne);
        BookableHourForCalendarDto bookableHourForCalendarDtoTwo =
                bookableHourService.convertBookableHourToBookableHourForCalendarDto(bookableHourTwo);

        assertEquals(bookableHourForCalendarDtoOne.getHour(), now);
        assertEquals(bookableHourForCalendarDtoTwo.getHour(), inOneHour);
        assertFalse(bookableHourForCalendarDtoOne.isBooked());
        assertTrue(bookableHourForCalendarDtoTwo.isBooked());
    }
}