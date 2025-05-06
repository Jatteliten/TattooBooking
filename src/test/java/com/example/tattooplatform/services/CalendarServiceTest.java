package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.bookabledate.BookableDateCalendarDto;
import com.example.tattooplatform.dto.bookablehour.BookableHourCalendarDto;
import com.example.tattooplatform.model.BookableDate;
import com.example.tattooplatform.model.BookableHour;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class CalendarServiceTest {
    @Autowired
    private CalendarService calendarService;
    @MockBean
    private BookableDateService bookableDateService;
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalTime TEN_O_CLOCK = LocalTime.of(10, 0);
    private static final LocalTime ELEVEN_O_CLOCK = LocalTime.of(11, 0);
    private static final LocalTime TWELVE_O_CLOCK = LocalTime.of(12, 0);
    private static final String DAYS = "days";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String PREVIOUS_MONTH = "prevMonth";
    private static final String PREVIOUS_MONTH_YEAR = "prevYear";
    private static final String NEXT_MONTH = "nextMonth";
    private static final String NEXT_MONTH_YEAR = "nextYear";

    @Test
    void createCalendarModel_shouldNotAddUnexpectedAttributes() {
        Model model = createJanuary2025Model();

        assertFalse(model.containsAttribute("unexpectedAttribute"));
    }

    @Test
    void createCalendarModel_shouldAddAllAttributesToCalendar(){
        Model model = new ExtendedModelMap();
        calendarService.createCalendarModel(model, LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        assertTrue(model.containsAttribute(DAYS));
        assertTrue(model.containsAttribute(MONTH));
        assertTrue(model.containsAttribute(YEAR));
        assertTrue(model.containsAttribute(PREVIOUS_MONTH));
        assertTrue(model.containsAttribute(PREVIOUS_MONTH_YEAR));
        assertTrue(model.containsAttribute(NEXT_MONTH));
        assertTrue(model.containsAttribute(NEXT_MONTH_YEAR));
    }

    @Test
    void createCalendarModel_shouldAddCorrectAmountOfDays() {
        Model model = createJanuary2025Model();

        List<?> days = (List<?>) model.getAttribute(DAYS);
        assertNotNull(days);
        assertEquals(35, days.size());
    }

    @Test
    void createCalendarModel_shouldAddCorrectMonth() {
        Model model = createJanuary2025Model();

        assertEquals(1, model.getAttribute(MONTH));
    }

    @Test
    void createCalendarModel_shouldAddCorrectYear() {
        Model model = createJanuary2025Model();

        assertEquals(2025, model.getAttribute(YEAR));
    }

    @Test
    void createCalendarModel_shouldAddCorrectPreviousMonth() {
        Model model = createJanuary2025Model();

        assertEquals(12, model.getAttribute(PREVIOUS_MONTH));
    }

    @Test
    void createCalendarModel_shouldAddSamePreviousMonthYear_whenMonthIsNotJanuary() {
        Model model = new ExtendedModelMap();
        calendarService.createCalendarModel(model, 2025, 2);

        assertEquals(2025, model.getAttribute(PREVIOUS_MONTH_YEAR));
    }

    @Test
    void createCalendarModel_shouldAddPreviousYear_asPreviousMonthsYear_whenMonthIsJanuary() {
        Model model = createJanuary2025Model();

        assertEquals(2024, model.getAttribute(PREVIOUS_MONTH_YEAR));
    }

    @Test
    void createCalendarModel_shouldAddCorrectNextMonth() {
        Model model = createJanuary2025Model();

        assertEquals(2, model.getAttribute(NEXT_MONTH));
    }

    @Test
    void createCalendarModel_shouldAddCorrectNextMonthsYear() {
        Model model = createJanuary2025Model();

        assertEquals(2025, model.getAttribute(NEXT_MONTH_YEAR));
    }

    @Test
    void createCalendarModel_shouldAddNextYear_asNextMonthsYear_whenMonthIsDecember() {
        Model model = new ExtendedModelMap();
        calendarService.createCalendarModel(model, 2024, 12);

        assertEquals(2025, model.getAttribute(NEXT_MONTH_YEAR));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldCreateFiveWeeksOnJanuaryFirst_ofYear2025(){
        assertEquals(35, createJanuary2025Calendar().size());
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldCreateFourWeeksOnFebruaryFirst_ofYear2021(){
        assertEquals(28, calendarService.createDaysInMonthFromSelectedDate(
                LocalDate.of(2021, 2, 1)).size());
    }

    @Test
    void createDaysInMonthFromSelectedDate_firstDayOfCalendar_shouldBeSetToNotCurrentMonth_inJanuary2025(){
        assertFalse(createJanuary2025Calendar().getFirst().isCurrentMonth());
    }

    @Test
    void createDaysInMonthFromSelectedDate_lastDayOfCalendar_shouldBeSetToNotCurrentMonth_inJanuary2025(){
        assertFalse(createJanuary2025Calendar().getLast().isCurrentMonth());
    }

    @Test
    void createDaysInMonthFromSelectedDate_middleOfCalendar_shouldAlwaysBeCurrentMonth(){
        List<BookableDateCalendarDto> bookableDateCalendarDto =
                calendarService.createDaysInMonthFromSelectedDate(LocalDate.now());
        assertTrue(bookableDateCalendarDto.get(bookableDateCalendarDto.size()/2).isCurrentMonth());
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldStartWithDecemberThirtieth_whenCreatingJanuary2025(){
        assertEquals(LocalDate.of(2024, 12, 30), createJanuary2025Calendar().getFirst().getDate());
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldEndWithFebruarySecond_whenCreatingJanuary2025(){
        List<BookableDateCalendarDto> january2025Calendar =
                calendarService.createDaysInMonthFromSelectedDate(LocalDate.of(2025, 1, 1));
        assertEquals(LocalDate.of(2025, 2, 2),
                january2025Calendar.getLast().getDate());
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetAllDaysToUnBookable_ifAllDatesAreInThePast(){
        List<BookableDateCalendarDto> january2025Calendar =
                calendarService.createDaysInMonthFromSelectedDate(LocalDate.of(2025, 1, 1));

        assertFalse(january2025Calendar.stream().anyMatch(BookableDateCalendarDto::isBookable));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetDateAsTouchUp_ifTouchUpDateExists(){
        LocalDate testDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        when(bookableDateService.getBookableDatesBetweenTwoDates(
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(List.of(BookableDate.builder()
                .date(testDate)
                .touchUp(true)
                .bookableHours(List.of())
                .build()));

        List<BookableDateCalendarDto> result = calendarService.createDaysInMonthFromSelectedDate(testDate);

        assertTrue(result.stream()
                .filter(dto -> dto.getDate().equals(testDate))
                .anyMatch(BookableDateCalendarDto::isTouchUp));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetDateAsDropIn_ifDropInDateExists(){
        LocalDate testDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        when(bookableDateService.getBookableDatesBetweenTwoDates(
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(List.of(BookableDate.builder()
                .date(testDate)
                .dropIn(true)
                .bookableHours(List.of())
                .build()));

        List<BookableDateCalendarDto> result = calendarService.createDaysInMonthFromSelectedDate(testDate);

        assertTrue(result.stream()
                .filter(dto -> dto.getDate().equals(testDate))
                .anyMatch(BookableDateCalendarDto::isDropIn));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetDateAsFullyBooked_ifFullyBookedDateExists(){
        LocalDate testDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        when(bookableDateService.getBookableDatesBetweenTwoDates(
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(List.of(BookableDate.builder()
                .date(testDate)
                .fullyBooked(true)
                .bookableHours(List.of())
                .build()));

        List<BookableDateCalendarDto> result = calendarService.createDaysInMonthFromSelectedDate(testDate);

        assertTrue(result.stream()
                .filter(dto -> dto.getDate().equals(testDate))
                .anyMatch(BookableDateCalendarDto::isFullyBooked));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldNotSetAnyDateToDropInOrTouchUp_ifNoneSuchDatesExist(){
        LocalDate testDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        bookableDateService.saveBookableDate(BookableDate.builder().date(testDate).build());

        List<BookableDateCalendarDto> calendar = calendarService.createDaysInMonthFromSelectedDate(testDate);

        assertFalse(calendar.stream().anyMatch(BookableDateCalendarDto::isDropIn));
        assertFalse(calendar.stream().anyMatch(BookableDateCalendarDto::isTouchUp));
        assertFalse(calendar.stream().anyMatch(BookableDateCalendarDto::isFullyBooked));
    }

    private List<BookableDateCalendarDto> createJanuary2025Calendar(){
        return calendarService.createDaysInMonthFromSelectedDate(LocalDate.of(2025, 1, 1));
    }

    private Model createJanuary2025Model() {
        Model model = new ExtendedModelMap();
        calendarService.createCalendarModel(model, 2025, 1);
        return model;
    }


    @Test
    void convertBookableDateToBookableDateCalendarDto_shouldSetCorrectProperties_fromBookableDate(){
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

        BookableDateCalendarDto bookableDateCalendarDto =
                calendarService.convertBookableDateToBookableDateCalendarDto(bookableDate);

        assertEquals(bookableDate.getDate(), bookableDateCalendarDto.getDate());
        assertEquals(bookableDate.isFullyBooked(), bookableDateCalendarDto.isFullyBooked());
        assertEquals(bookableDate.isDropIn(), bookableDateCalendarDto.isDropIn());
        assertEquals(bookableDate.isTouchUp(), bookableDateCalendarDto.isTouchUp());
        assertEquals(bookableDate.getBookableHours().size(), bookableDateCalendarDto.getHours().size());
    }

    @Test
    void convertBookableDateToBookableDateCalendarDto_shouldSortCorrectly_andBuildCorrectHourStrings(){
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

        BookableDateCalendarDto bookableDateCalendarDto =
                calendarService.convertBookableDateToBookableDateCalendarDto(bookableDate);

        assertEquals("10:00-false", bookableDateCalendarDto.getHours().get(0));
        assertEquals("11:00-true", bookableDateCalendarDto.getHours().get(1));
        assertEquals("12:00-false", bookableDateCalendarDto.getHours().get(2));
    }

    @Test
    void convertBookableHourToBookableHourCalendarDto_shouldConvertAttributesCorrectly(){
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(TEN_O_CLOCK)
                .booked(false)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(ELEVEN_O_CLOCK)
                .booked(true)
                .build();

        BookableHourCalendarDto bookableHourCalendarDtoOne =
                calendarService.convertBookableHourToBookableHourCalendarDto(bookableHourOne);
        BookableHourCalendarDto bookableHourCalendarDtoTwo =
                calendarService.convertBookableHourToBookableHourCalendarDto(bookableHourTwo);

        assertEquals(TEN_O_CLOCK, bookableHourCalendarDtoOne.getHour());
        assertEquals(ELEVEN_O_CLOCK, bookableHourCalendarDtoTwo.getHour());
        assertFalse(bookableHourCalendarDtoOne.isBooked());
        assertTrue(bookableHourCalendarDtoTwo.isBooked());
    }

}