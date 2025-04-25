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
    private BookableHourRepo bookableHourRepo;
    @Autowired
    private BookableHourService bookableHourService;

    private static final LocalTime TEN_O_CLOCK = LocalTime.of(10, 0);
    private static final LocalTime ELEVEN_O_CLOCK = LocalTime.of(11, 0);
    private static final LocalTime TWELVE_O_CLOCK = LocalTime.of(12, 0);

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
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(ELEVEN_O_CLOCK)
                .booked(false)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(TWELVE_O_CLOCK)
                .booked(false)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableHourService.setBookableHoursInBookableDateToBookedBetweenStartAndEndTime(
                bookableDate, TEN_O_CLOCK, TEN_O_CLOCK.plusHours(3));

        assertTrue(bookableHourOne.isBooked());
        assertTrue(bookableHourTwo.isBooked());
    }

    @Test
    void setBookableHoursInBookableDateToBookedBetweenStartAndEndTime_shouldNotSetBookableHoursOutsideHoursRangeToBooked(){
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(TEN_O_CLOCK)
                .booked(false)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(ELEVEN_O_CLOCK)
                .booked(false)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableHourService.setBookableHoursInBookableDateToBookedBetweenStartAndEndTime(
                bookableDate, TWELVE_O_CLOCK, TWELVE_O_CLOCK.plusHours(1));

        assertFalse(bookableHourOne.isBooked());
        assertFalse(bookableHourTwo.isBooked());
    }

    @Test
    void convertBookableHourToBookableHourForCalendarDto_shouldConvertAttributesCorrectly(){
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(TEN_O_CLOCK)
                .booked(false)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(ELEVEN_O_CLOCK)
                .booked(true)
                .build();

        BookableHourForCalendarDto bookableHourForCalendarDtoOne =
                bookableHourService.convertBookableHourToBookableHourForCalendarDto(bookableHourOne);
        BookableHourForCalendarDto bookableHourForCalendarDtoTwo =
                bookableHourService.convertBookableHourToBookableHourForCalendarDto(bookableHourTwo);

        assertEquals(bookableHourForCalendarDtoOne.getHour(), TEN_O_CLOCK);
        assertEquals(bookableHourForCalendarDtoTwo.getHour(), ELEVEN_O_CLOCK);
        assertFalse(bookableHourForCalendarDtoOne.isBooked());
        assertTrue(bookableHourForCalendarDtoTwo.isBooked());
    }
}