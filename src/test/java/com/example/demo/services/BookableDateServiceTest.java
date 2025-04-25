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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class BookableDateServiceTest {
    @Autowired
    private BookableDateRepo bookableDateRepo;
    @Autowired
    private BookableDateService bookableDateService;

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalTime TEN_O_CLOCK = LocalTime.of(10, 0);
    private static final LocalTime ELEVEN_O_CLOCK = LocalTime.of(11, 0);
    private static final LocalTime TWELVE_O_CLOCK = LocalTime.of(12, 0);

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
                .date(TODAY)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .build()))
                .build();

        bookableDateService.saveBookableDate(bookableDate);

        assertNotNull(bookableDateRepo.findById(bookableDate.getId()));
    }

    @Test
    void saveBookableDate_shouldSetBookableDateToFullyBooked_ifAllBookableHoursAreBooked(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        bookableDateService.saveBookableDate(bookableDate);

        assertTrue(bookableDate.isFullyBooked());
    }

    @Test
    void saveBookableDate_shouldSetBookableDateToNotFullyBooked_ifAnyBookableHourIsAvailable(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build(),
                        BookableHour.builder()
                        .hour(ELEVEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        bookableDateService.saveBookableDate(bookableDate);

        assertFalse(bookableDate.isFullyBooked());
    }

    @Test
    void saveBookableDate_shouldSetBookableDateToFullyBooked_ifAllBookableHoursAreBooked_whenUpdatingBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
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
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();
        BookableDate bookableDateTwo = BookableDate.builder()
                .date(TODAY.plusDays(1))
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();

        bookableDateService.saveListOfBookableDates(List.of(bookableDateOne, bookableDateTwo));

        assertEquals(2, bookableDateRepo.findAll().size());
    }

    @Test
    void getBookableDateByDate_shouldGetCorrectBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();
        bookableDateRepo.save(bookableDate);

        assertEquals(bookableDate.getId(), bookableDateService.getBookableDateByDate(TODAY).getId());
    }

    @Test
    void getBookableDateByDate_shouldNotGetBookableDate_ifDateIsIncorrect(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();
        bookableDateRepo.save(bookableDate);

        assertNull(bookableDateService.getBookableDateByDate(TODAY.plusDays(1)));
    }

    @Test
    void getByBookableDateBetween_shouldFindBookableDate_insideDateRange(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.save(bookableDate);

        assertEquals(1, bookableDateService.getBookableDatesBetweenTwoDates(
                TODAY.minusDays(1), TODAY.plusDays(1)).size());
    }

    @Test
    void getByBookableDateBetween_shouldNotFindBookableDate_outsideDateRange(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY.minusDays(1))
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.save(bookableDate);

        assertEquals(0, bookableDateService.getBookableDatesBetweenTwoDates(TODAY, TODAY.plusDays(1)).size());
    }

    @Test
    void getByBookableDateBetween_shouldFindAllBookableDates_insideDateRange(){
        BookableDate bookableDateOne = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();
        BookableDate bookableDateTwo = BookableDate.builder()
                .date(TODAY.plusDays(1))
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.saveAll(List.of(bookableDateOne, bookableDateTwo));

        assertEquals(2, bookableDateService.getBookableDatesBetweenTwoDates(
                TODAY.minusDays(1), TODAY.plusDays(2)).size());
    }

    @Test
    void getByBookableDateBetween_shouldFindCorrectBookableDate_insideDateRange(){
        BookableDate bookableDateOne = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();
        BookableDate bookableDateTwo = BookableDate.builder()
                .date(TODAY.plusDays(5))
                .fullyBooked(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build()))
                .build();

        bookableDateRepo.saveAll(List.of(bookableDateOne, bookableDateTwo));

        LocalDate yesterday = TODAY.minusDays(1);
        LocalDate tomorrow = TODAY.plusDays(1);
        List<BookableDate> bookableDateList = bookableDateService.getBookableDatesBetweenTwoDates(
                yesterday, tomorrow);

        assertEquals(1, bookableDateList.size());
        assertEquals(bookableDateOne.getId(), bookableDateList.get(0).getId());
    }

    @Test
    void convertBookableDateToBookableDateForCalendarDto_shouldSetCorrectProperties_fromBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .dropIn(false)
                .touchUp(false)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
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
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(TEN_O_CLOCK)
                                .booked(false)
                                .build(),
                        BookableHour.builder()
                                .hour(ELEVEN_O_CLOCK)
                                .booked(true)
                                .build(),
                        BookableHour.builder()
                                .hour(TWELVE_O_CLOCK)
                                .booked(false)
                                .build()))
                .build();

        BookableDateForCalendarDto bookableDateForCalendarDto =
                bookableDateService.convertBookableDateToBookableDateForCalendarDto(bookableDate);

        assertEquals("10:00-false",bookableDateForCalendarDto.getHours().get(0));
        assertEquals("11:00-true",bookableDateForCalendarDto.getHours().get(1));
        assertEquals("12:00-false",bookableDateForCalendarDto.getHours().get(2));
    }

    @Test
    void setBookableDateToFullyBookedIfAllHoursAreBooked_shouldSetBookableDateToFullyBooked_ifAllHoursAreBooked(){
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(TEN_O_CLOCK)
                                .booked(true)
                                .build(),
                        BookableHour.builder()
                                .hour(TWELVE_O_CLOCK)
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
                .date(TODAY)
                .fullyBooked(false)
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(TEN_O_CLOCK)
                                .booked(false)
                                .build(),
                        BookableHour.builder()
                                .hour(TWELVE_O_CLOCK)
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
                        .hour(TEN_O_CLOCK)
                        .booked(false)
                        .build();
        BookableHour bookableHourTwo =
                BookableHour.builder()
                        .hour(TWELVE_O_CLOCK)
                        .booked(false)
                        .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
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
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(TWELVE_O_CLOCK)
                .booked(true)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(TWELVE_O_CLOCK.plusHours(1))
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
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
                .date(TODAY.atTime(ELEVEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK.plusHours(1)))
                .build();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(TWELVE_O_CLOCK)
                .booked(true)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(TWELVE_O_CLOCK.plusHours(2))
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
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
                .date(TODAY.atTime(ELEVEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK.plusHours(5)))
                .build();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(TWELVE_O_CLOCK)
                .booked(true)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(TWELVE_O_CLOCK.plusHours(4))
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
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
        DateForm dateForm = DateForm.builder()
                .dateList(List.of(
                        DateEntry.builder()
                        .date(TODAY)
                        .hours(List.of(TEN_O_CLOCK))
                        .build(),
                        DateEntry.builder()
                        .date(TODAY.plusDays(1))
                        .hours(List.of(TWELVE_O_CLOCK))
                        .build()))
                .build();
        List<BookableDate> bookableDates = bookableDateService.createBookableDatesFromDateForm(dateForm);

        assertEquals(bookableDates.get(0).getDate(), TODAY);
        assertEquals(bookableDates.get(1).getDate(), TODAY.plusDays(1));
        assertEquals(bookableDates.get(0).getBookableHours().get(0).getHour(), TEN_O_CLOCK);
        assertEquals(bookableDates.get(1).getBookableHours().get(0).getHour(), TWELVE_O_CLOCK);
    }

    @Test
    void createBookableDatesFromDateForm_shouldReturnCorrectBookingTypes(){
        DateForm dateForm = DateForm.builder()
                .dateList(List.of(
                        DateEntry.builder()
                                .date(TODAY)
                                .hours(List.of(TWELVE_O_CLOCK))
                                .build(),
                        DateEntry.builder()
                                .date(TODAY.plusDays(1))
                                .type("touchup")
                                .hours(List.of(TWELVE_O_CLOCK))
                                .build(),
                        DateEntry.builder()
                                .date(TODAY.plusDays(1))
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
                                .date(TODAY.plusDays(1))
                                .type("dropin")
                                .build()))
                .build();
        List<BookableDate> bookableDates = bookableDateService.createBookableDatesFromDateForm(dateForm);

        assertEquals(TWELVE_O_CLOCK, bookableDates.get(0).getBookableHours().get(0).getHour());
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
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(TODAY.atTime(ELEVEN_O_CLOCK))
                        .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                        .build());

        assertTrue(bookableDateService.checkIfHourIsAvailable(TEN_O_CLOCK, bookings));
    }

    @Test
    void checkIfHourIsAvailable_shouldReturnFalse_ifTimeIsBooked(){
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(TODAY.atTime(TEN_O_CLOCK))
                        .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                        .build());

        assertFalse(bookableDateService.checkIfHourIsAvailable(ELEVEN_O_CLOCK, bookings));
    }

    @Test
    void checkIfHourIsAvailable_shouldReturnTrue_ifBookingEndsWhenHourStarts(){
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(TODAY.atTime(TEN_O_CLOCK))
                        .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                        .build());

        assertTrue(bookableDateService.checkIfHourIsAvailable(TWELVE_O_CLOCK, bookings));
    }

    @Test
    void checkIfHourIsAvailable_shouldReturnFalse_ifBookingStartsWhenHourStarts(){
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(TODAY.atTime(TEN_O_CLOCK))
                        .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                        .build());

        assertTrue(bookableDateService.checkIfHourIsAvailable(TEN_O_CLOCK, bookings));
    }

    @Test
    void checkIfBookableHourExistsAtBookableDate_shouldReturnTrue_IfBookableHourExists(){
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .build()))
                .build();

        assertTrue(bookableDateService.checkIfBookableHourExistsAtBookableDate(bookableDate, TEN_O_CLOCK));
    }

    @Test
    void checkIfBookableHourExistsAtBookableDate_shouldReturnFalse_IfBookableHourDoesNotExists(){
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(List.of(
                        BookableHour.builder()
                                .hour(TEN_O_CLOCK)
                                .build(),
                        BookableHour.builder()
                                .hour(ELEVEN_O_CLOCK)
                                .build()))
                .build();

        assertFalse(bookableDateService.checkIfBookableHourExistsAtBookableDate(bookableDate, TWELVE_O_CLOCK));
    }

    @Test
    void addHourToBookableDate_shouldAddBookableHourToBookableDate(){
        BookableDate bookableDate = BookableDate.builder()
                .bookableHours(new ArrayList<>())
                .build();
        bookableDateRepo.save(bookableDate);

        bookableDateService.addHourToBookableDate(bookableDate, TEN_O_CLOCK);

        assertEquals(1, bookableDate.getBookableHours().size());
    }

    @Test
    void addHourToBookableDate_shouldSetBookableHourToAvailable(){
        BookableDate bookableDate = BookableDate.builder()
                .fullyBooked(true)
                .bookableHours(new ArrayList<>())
                .build();
        bookableDateRepo.save(bookableDate);

        bookableDateService.addHourToBookableDate(bookableDate, TEN_O_CLOCK);

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