package com.example.demo.services;

import com.example.demo.dtos.bookabledatedtos.BookableDateForCalendarDto;
import com.example.demo.dtos.bookabledatedtos.DateEntry;
import com.example.demo.dtos.bookabledatedtos.DateForm;
import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.model.Booking;
import com.example.demo.repos.BookableDateRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class BookableDateServiceTest {
    @Autowired
    BookableDateRepo bookableDateRepo;
    @Autowired
    BookableDateService bookableDateService;

    @AfterEach
    void deleteAllData(){
        bookableDateRepo.deleteAll();
    }

    @Test
    void saveBookableDateShouldThrowExceptionIfNoBookableHoursAreAssociatedToBookableDate(){
        BookableDate bookableDate = BookableDate.builder().build();

        assertThrows(NullPointerException.class, () -> bookableDateService.saveBookableDate(bookableDate));
    }

    @Test
    void saveBookableDate_shouldSaveBookableDate_ifItHasAtLeastOneBookableHour(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .build()))
                .build();

        bookableDateService.saveBookableDate(bookableDate);

        assertNotNull(bookableDateRepo.findById(bookableDate.getId()));
    }

    @Test
    void saveBookableDate_shouldSetBookableDateToFullyBooked_ifAllBookableHoursAreBooked(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(true)
                        .build()))
                .build();

        bookableDateService.saveBookableDate(bookableDate);

        assertTrue(bookableDate.isFullyBooked());
    }

    @Test
    void saveBookableDate_shouldSetBookableDateToNotFullyBooked_ifAnyBookableHourIsAvailable(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build(),
                        BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(true)
                        .build()))
                .build();

        bookableDateService.saveBookableDate(bookableDate);

        assertFalse(bookableDate.isFullyBooked());
    }

    @Test
    void saveBookableDate_shouldSetBookableDateToFullyBooked_ifAllBookableHoursAreBooked_whenUpdatingBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();

        bookableDateService.saveBookableDate(bookableDate);

        assertFalse(bookableDate.isFullyBooked());

        bookableDate.getBookableHours().get(0).setBooked(true);
        bookableDateService.saveBookableDate(bookableDate);

        assertTrue(bookableDate.isFullyBooked());
    }

    @Test
    void saveListOfBookableDates_shouldSaveAllBookableDatesInList(){
        BookableDate bookableDateOne = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();
        BookableDate bookableDateTwo = BookableDate.builder()
                .date(LocalDate.now().plusDays(1))
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();

        bookableDateService.saveListOfBookableDates(List.of(bookableDateOne, bookableDateTwo));

        assertEquals(2, bookableDateRepo.findAll().size());
    }

    @Test
    void getBookableDateByDate_shouldGetCorrectBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();
        bookableDateRepo.save(bookableDate);

        assertEquals(bookableDate.getId(), bookableDateService.getBookableDateByDate(LocalDate.now()).getId());
    }

    @Test
    void getBookableDateByDate_shouldNotGetBookableDate_ifDateIsIncorrect(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();
        bookableDateRepo.save(bookableDate);

        assertNull(bookableDateService.getBookableDateByDate(LocalDate.now().plusDays(1)));
    }

    @Test
    void getByBookableDateBetween_shouldFindBookableDate_insideDateRange(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.save(bookableDate);

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        assertEquals(1, bookableDateService.getBookableDatesBetweenTwoDates(yesterday, tomorrow).size());
    }

    @Test
    void getByBookableDateBetween_shouldNotFindBookableDate_outsideDateRange(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.save(bookableDate);

        LocalDate yesterday = LocalDate.now().plusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(2);

        assertEquals(0, bookableDateService.getBookableDatesBetweenTwoDates(yesterday, tomorrow).size());
    }

    @Test
    void getByBookableDateBetween_shouldFindAllBookableDates_insideDateRange(){
        BookableDate bookableDateOne = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();
        BookableDate bookableDateTwo = BookableDate.builder()
                .date(LocalDate.now().plusDays(1))
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.saveAll(List.of(bookableDateOne, bookableDateTwo));

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);

        assertEquals(2, bookableDateService.getBookableDatesBetweenTwoDates(
                yesterday, dayAfterTomorrow).size());
    }

    @Test
    void getByBookableDateBetween_shouldFindCorrectBookableDate_insideDateRange(){
        BookableDate bookableDateOne = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();
        BookableDate bookableDateTwo = BookableDate.builder()
                .date(LocalDate.now().plusDays(5))
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.saveAll(List.of(bookableDateOne, bookableDateTwo));

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<BookableDate> bookableDateList = bookableDateService.getBookableDatesBetweenTwoDates(
                yesterday, tomorrow);

        assertEquals(1, bookableDateList.size());
        assertEquals(bookableDateOne.getId(), bookableDateList.get(0).getId());
    }

    @Test
    void convertBookableDateToBookableDateForCalendarDto_shouldSetCorrectProperties_fromBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .dropIn(false)
                .touchUp(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.now())
                        .booked(false)
                        .build()))
                .build();

        BookableDateForCalendarDto bookableDateForCalendarDto =
                bookableDateService.convertBookableDateToBookableDateForCalendarDto(bookableDate);

        assertEquals(bookableDate.getDate(), bookableDateForCalendarDto.getDate());
        assertEquals(bookableDate.isFullyBooked(), bookableDateForCalendarDto.isFullyBooked());
        assertEquals(bookableDate.isDropIn(), bookableDateForCalendarDto.isDropIn());
        assertEquals(bookableDate.isTouchUp(), bookableDateForCalendarDto.isTouchUp());
        assertEquals(bookableDate.getBookableHours().size(), bookableDateForCalendarDto.getHours().size());
    }

    @Test
    void convertBookableDateToBookableDateForCalendarDto_shouldSortCorrectly_andBuildCorrectHourStrings(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(LocalTime.of(16, 0))
                                .booked(false)
                                .build(),
                        BookableHour.builder()
                                .hour(LocalTime.of(12, 0))
                                .booked(false)
                                .build(),
                        BookableHour.builder()
                                .hour(LocalTime.of(14, 0))
                                .booked(true)
                                .build()))
                .build();

        BookableDateForCalendarDto bookableDateForCalendarDto =
                bookableDateService.convertBookableDateToBookableDateForCalendarDto(bookableDate);

        assertEquals("12:00-false",bookableDateForCalendarDto.getHours().get(0));
        assertEquals("14:00-true",bookableDateForCalendarDto.getHours().get(1));
        assertEquals("16:00-false",bookableDateForCalendarDto.getHours().get(2));
    }

    @Test
    void setBookableDateToFullyBookedIfAllHoursAreBooked_shouldSetBookableDateToFullyBooked_ifAllHoursAreBooked(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(LocalTime.of(16, 0))
                                .booked(true)
                                .build(),
                        BookableHour.builder()
                                .hour(LocalTime.of(12, 0))
                                .booked(true)
                                .build()))
                .build();

        assertFalse(bookableDate.isFullyBooked());

        bookableDateService.setBookableDateToFullyBookedIfAllHoursAreBooked(bookableDate);

        assertTrue(bookableDate.isFullyBooked());
    }

    @Test
    void setBookableDateToFullyBookedIfAllHoursAreBooked_shouldNotSetBookableDateToFullyBooked_ifAllHoursAreNotBooked(){
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(LocalTime.of(16, 0))
                                .booked(false)
                                .build(),
                        BookableHour.builder()
                                .hour(LocalTime.of(12, 0))
                                .booked(true)
                                .build()))
                .build();

        assertFalse(bookableDate.isFullyBooked());

        bookableDateService.setBookableDateToFullyBookedIfAllHoursAreBooked(bookableDate);

        assertFalse(bookableDate.isFullyBooked());
    }

    @Test
    void setBookableDateAndHoursToUnavailable_shouldSetBookableDateAndAllBookableHoursToBooked(){
        BookableHour bookableHourOne = BookableHour.builder()
                        .hour(LocalTime.of(16, 0))
                        .booked(false)
                        .build();
        BookableHour bookableHourTwo =
                BookableHour.builder()
                        .hour(LocalTime.of(14, 0))
                        .booked(false)
                        .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(false)
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableDateService.setBookableDateAndHoursToUnavailable(bookableDate);

        assertTrue(bookableDate.isFullyBooked());
        assertTrue(bookableHourOne.isBooked());
        assertTrue(bookableHourTwo.isBooked());
    }

    @Test
    void setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked_shouldSetDateAndHoursToAvailable_ifAllHoursAreNotBooked(){
        Booking booking = Booking.builder()
                .date(LocalDate.now().atTime(10, 0))
                .endTime(LocalDate.now().atTime(11, 0))
                .build();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(LocalTime.of(12, 0))
                .booked(true)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(LocalTime.of(16, 0))
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(true)
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableDateService.setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(bookableDate, List.of(booking));

        assertFalse(bookableDate.isFullyBooked());
        assertFalse(bookableHourOne.isBooked());
        assertFalse(bookableHourTwo.isBooked());
    }

    @Test
    void setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked_shouldNotSetAffectedHoursToAvailable_ifHoursAreBooked(){
        Booking booking = Booking.builder()
                .date(LocalDate.now().atTime(11, 0))
                .endTime(LocalDate.now().atTime(13, 0))
                .build();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(LocalTime.of(12, 0))
                .booked(true)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(LocalTime.of(16, 0))
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(true)
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableDateService.setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(bookableDate, List.of(booking));

        assertFalse(bookableDate.isFullyBooked());
        assertTrue(bookableHourOne.isBooked());
        assertFalse(bookableHourTwo.isBooked());
    }

    @Test
    void setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked_shouldNotSetAnythingToAvailable_ifAllHoursAreBooked(){
        Booking booking = Booking.builder()
                .date(LocalDate.now().atTime(11, 0))
                .endTime(LocalDate.now().atTime(17, 0))
                .build();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(LocalTime.of(12, 0))
                .booked(true)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(LocalTime.of(16, 0))
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(LocalDate.now())
                .fullyBooked(true)
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        bookableDateService.setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(bookableDate, List.of(booking));

        assertTrue(bookableDate.isFullyBooked());
        assertTrue(bookableHourOne.isBooked());
        assertTrue(bookableHourTwo.isBooked());
    }

    @Test
    void checkIfBookableHoursInBookableDateAreAllBooked_shouldReturnFalse_ifAllHoursAreNotBooked(){
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(
                        BookableHour.builder()
                                .booked(true)
                                .build(),
                        BookableHour.builder()
                                .booked(false)
                                .build()))
                .build();

        assertFalse(bookableDateService.checkIfBookableHoursInBookableDateAreAllBooked(bookableDate));
    }

    @Test
    void checkIfBookableHoursInBookableDateAreAllBooked_shouldReturnTrue_ifAllHoursAreBooked(){
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(
                        BookableHour.builder()
                                .booked(true)
                                .build(),
                        BookableHour.builder()
                                .booked(true)
                                .build()))
                .build();

        assertTrue(bookableDateService.checkIfBookableHoursInBookableDateAreAllBooked(bookableDate));
    }

    @Test
    void createBookableDatesFromDateForm_shouldReturnCorrectBookableDates_andBookableHours(){
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalTime twelveSharp = LocalTime.of(12, 0);
        LocalTime fourteenSharp = LocalTime.of(14, 0);
        DateForm dateForm = DateForm.builder()
                .dateList(List.of(
                        DateEntry.builder()
                        .date(today)
                        .hours(List.of(twelveSharp))
                        .build(),
                        DateEntry.builder()
                        .date(tomorrow)
                        .hours(List.of(fourteenSharp))
                        .build()))
                .build();
        List<BookableDate> bookableDates = bookableDateService.createBookableDatesFromDateForm(dateForm);

        assertEquals(bookableDates.get(0).getDate(), today);
        assertEquals(bookableDates.get(1).getDate(), tomorrow);
        assertEquals(bookableDates.get(0).getBookableHours().get(0).getHour(), twelveSharp);
        assertEquals(bookableDates.get(1).getBookableHours().get(0).getHour(), fourteenSharp);
    }

    @Test
    void createBookableDatesFromDateForm_shouldReturnCorrectBookingTypes(){
        LocalTime twelveSharp = LocalTime.of(12, 0);
        DateForm dateForm = DateForm.builder()
                .dateList(List.of(
                        DateEntry.builder()
                                .date(LocalDate.now())
                                .hours(List.of(twelveSharp))
                                .build(),
                        DateEntry.builder()
                                .date(LocalDate.now().plusDays(1))
                                .type("touchup")
                                .hours(List.of(twelveSharp))
                                .build(),
                        DateEntry.builder()
                                .date(LocalDate.now().plusDays(1))
                                .type("dropin")
                                .build()))
                .build();
        List<BookableDate> bookableDates = bookableDateService.createBookableDatesFromDateForm(dateForm);

        assertFalse(bookableDates.get(0).isTouchUp());
        assertFalse(bookableDates.get(0).isDropIn());
        assertTrue(bookableDates.get(1).isTouchUp());
        assertFalse(bookableDates.get(1).isDropIn());
        assertFalse(bookableDates.get(2).isTouchUp());
        assertTrue(bookableDates.get(2).isDropIn());
    }

    @Test
    void createBookableDatesFromDateForm_shouldSetDropinToCorrectStartHour(){
        DateForm dateForm = DateForm.builder()
                .dateList(List.of(DateEntry.builder()
                                .date(LocalDate.now().plusDays(1))
                                .type("dropin")
                                .build()))
                .build();
        List<BookableDate> bookableDates = bookableDateService.createBookableDatesFromDateForm(dateForm);

        assertEquals(LocalTime.of(12, 0), bookableDates.get(0).getBookableHours().get(0).getHour());
    }

    @Test
    void getAvailableDatesBetweenTwoDates_shouldReturnCorrectAvailableDates(){
        LocalDate monday = LocalDate.of(2025, 4, 14);
        LocalDate friday = LocalDate.of(2025, 4, 18);

        List<LocalDate> localDates = bookableDateService.getAvailableDatesBetweenTwoDates(monday, friday);

        assertEquals(5, localDates.size());
    }

    @Test
    void getAvailableDatesBetweenTwoDates_shouldNotReturnSundays(){
        LocalDate sunday = LocalDate.of(2025, 4, 20);

        List<LocalDate> localDates = bookableDateService.getAvailableDatesBetweenTwoDates(sunday, sunday);

        assertEquals(0, localDates.size());
    }

    @Test
    void getAvailableDatesBetweenTwoDates_shouldNotReturnAlreadyExistingBookableDates() {
        LocalDate monday = LocalDate.of(2025, 4, 14);
        BookableDate bookableDate = BookableDate.builder().date(monday).build();
        bookableDateRepo.save(bookableDate);

        List<LocalDate> localDates = bookableDateService.getAvailableDatesBetweenTwoDates(monday, monday);

        assertEquals(0, localDates.size());
    }

    @Test
    void getAvailableDatesBetweenTwoDates_shouldReturnDatesAroundExistingBookableDate(){
        LocalDate monday = LocalDate.of(2025, 4, 14);
        LocalDate tuesday = LocalDate.of(2025, 4, 15);
        LocalDate wednesday = LocalDate.of(2025, 4, 16);

        BookableDate bookableDate = BookableDate.builder().date(tuesday).build();
        bookableDateRepo.save(bookableDate);

        List<LocalDate> localDates = bookableDateService.getAvailableDatesBetweenTwoDates(monday, wednesday);

        assertEquals(2, localDates.size());
    }

    @Test
    void checkIfHourIsAvailable_shouldReturnTrue_ifHourIsAvailable(){
        LocalTime now = LocalTime.now();
        LocalDateTime inOneHour = LocalDateTime.now().plusHours(1);
        LocalDateTime inTwoHours = LocalDateTime.now().plusHours(2);

        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(inOneHour)
                        .endTime(inTwoHours)
                        .build());

        assertTrue(bookableDateService.checkIfHourIsAvailable(now, bookings));
    }

    @Test
    void checkIfHourIsAvailable_shouldReturnFalse_ifTimeIsBooked(){
        LocalDateTime now = LocalDateTime.now();
        LocalTime inOneHour = LocalTime.now().plusHours(1);
        LocalDateTime inTwoHours = LocalDateTime.now().plusHours(2);
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(now)
                        .endTime(inTwoHours)
                        .build());

        assertFalse(bookableDateService.checkIfHourIsAvailable(inOneHour, bookings));
    }

    @Test
    void checkIfHourIsAvailable_shouldReturnTrue_ifBookingEndsWhenHourStarts(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inTwoHours = LocalDateTime.now().plusHours(2);
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(now)
                        .endTime(inTwoHours)
                        .build());

        assertTrue(bookableDateService.checkIfHourIsAvailable(inTwoHours.toLocalTime(), bookings));
    }

    @Test
    void checkIfHourIsAvailable_shouldReturnFalse_ifBookingStartsWhenHourStarts(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inTwoHours = LocalDateTime.now().plusHours(2);
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(now)
                        .endTime(inTwoHours)
                        .build());

        assertTrue(bookableDateService.checkIfHourIsAvailable(now.toLocalTime(), bookings));
    }

    @Test
    void checkIfBookableHourExistsAtBookableDate_shouldReturnTrue_IfBookableHourExists(){
        LocalTime now = LocalTime.now();
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(BookableHour.builder()
                        .hour(now)
                        .build()))
                .build();

        assertTrue(bookableDateService.checkIfBookableHourExistsAtBookableDate(bookableDate, now));
    }

    @Test
    void checkIfBookableHourExistsAtBookableDate_shouldReturnFalse_IfBookableHourDoesNotExists(){
        LocalTime now = LocalTime.now();
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(now.plusHours(1))
                                .build(),
                        BookableHour.builder()
                                .hour(now.minusHours(1))
                                .build()))
                .build();

        assertFalse(bookableDateService.checkIfBookableHourExistsAtBookableDate(bookableDate, now));
    }

    @Test
    void addHourToBookableDate_shouldAddBookableHourToBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(new ArrayList<>())
                .build();
        bookableDateRepo.save(bookableDate);

        bookableDateService.addHourToBookableDate(bookableDate, LocalTime.now());

        assertEquals(1, bookableDate.getBookableHours().size());
    }

    @Test
    void addHourToBookableDate_shouldSetBookableHourToAvailable(){
        BookableDate bookableDate = BookableDate.builder()
                .fullyBooked(true)
                .bookableHours(new ArrayList<>())
                .build();
        bookableDateRepo.save(bookableDate);

        bookableDateService.addHourToBookableDate(bookableDate, LocalTime.now());

        assertFalse(bookableDate.isFullyBooked());
    }

    @Test
    void setBookableDateAndBookableHourToAvailable_shouldSetBookableDateAndBookableHourToAvailable(){
        BookableHour bookableHour = BookableHour.builder()
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .fullyBooked(true)
                .bookableHours(List.of(bookableHour))
                .build();

        bookableDateService.setBookableDateAndBookableHourToAvailable(bookableDate, bookableHour);

        assertFalse(bookableDate.isFullyBooked());
        assertFalse(bookableHour.isBooked());
    }

}