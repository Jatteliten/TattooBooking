package com.example.demo.services;

import com.example.demo.dtos.bookabledatedtos.BookableDateForCalendarDto;
import com.example.demo.model.BookableDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class CalendarServiceTest {
    @Autowired
    private CalendarService calendarService;
    @MockBean
    private BookableDateService bookableDateService;
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
        List<BookableDateForCalendarDto> bookableDateForCalendarDto =
                calendarService.createDaysInMonthFromSelectedDate(LocalDate.now());
        assertTrue(bookableDateForCalendarDto.get(bookableDateForCalendarDto.size()/2).isCurrentMonth());
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldStartWithDecemberThirtieth_whenCreatingJanuary2025(){
        assertEquals(LocalDate.of(2024, 12, 30), createJanuary2025Calendar().getFirst().getDate());
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldEndWithFebruarySecond_whenCreatingJanuary2025(){
        List<BookableDateForCalendarDto> january2025Calendar =
                calendarService.createDaysInMonthFromSelectedDate(LocalDate.of(2025, 1, 1));
        assertEquals(LocalDate.of(2025, 2, 2),
                january2025Calendar.getLast().getDate());
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetAllDaysToUnBookable_ifAllDatesAreInThePast(){
        List<BookableDateForCalendarDto> january2025Calendar =
                calendarService.createDaysInMonthFromSelectedDate(LocalDate.of(2025, 1, 1));

        assertFalse(january2025Calendar.stream().anyMatch(BookableDateForCalendarDto::isBookable));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetDateAsTouchUp_ifTouchUpDateExists(){
        List<BookableDateForCalendarDto> calendar = calendarService.createDaysInMonthFromSelectedDate(
                mockFutureJanuaryFirstWithTouchUpDropInOrFullyBooked(true, false, false));

        assertTrue(calendar.stream().anyMatch(dto -> dto.getDate().getDayOfMonth() == 1 && dto.isTouchUp()));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetDateAsDropIn_ifDropInDateExists(){
        List<BookableDateForCalendarDto> calendar = calendarService.createDaysInMonthFromSelectedDate(
                mockFutureJanuaryFirstWithTouchUpDropInOrFullyBooked(false, true, false));

        assertTrue(calendar.stream().anyMatch(dto -> dto.getDate().getDayOfMonth() == 1 && dto.isDropIn()));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldSetDateAsFullyBooked_ifFullyBookedDateExists(){
        List<BookableDateForCalendarDto> calendar = calendarService.createDaysInMonthFromSelectedDate(
                mockFutureJanuaryFirstWithTouchUpDropInOrFullyBooked(false, false, true));

        assertTrue(calendar.stream().anyMatch(dto -> dto.getDate().getDayOfMonth() == 1 && dto.isFullyBooked()));
    }

    @Test
    void createDaysInMonthFromSelectedDate_shouldNotSetAnyDateToDropInOrTouchUp_ifNoneSuchDatesExist(){
        List<BookableDateForCalendarDto> calendar = calendarService.createDaysInMonthFromSelectedDate(
                mockFutureJanuaryFirstWithTouchUpDropInOrFullyBooked(false, false, false));

        assertFalse(calendar.stream().anyMatch(BookableDateForCalendarDto::isDropIn));
        assertFalse(calendar.stream().anyMatch(BookableDateForCalendarDto::isTouchUp));
        assertFalse(calendar.stream().anyMatch(BookableDateForCalendarDto::isFullyBooked));
    }

    private List<BookableDateForCalendarDto> createJanuary2025Calendar(){
        return calendarService.createDaysInMonthFromSelectedDate(LocalDate.of(2025, 1, 1));
    }

    private Model createJanuary2025Model() {
        Model model = new ExtendedModelMap();
        calendarService.createCalendarModel(model, 2025, 1);
        return model;
    }

    private LocalDate mockFutureJanuaryFirstWithTouchUpDropInOrFullyBooked(
            boolean touchUp, boolean dropIn, boolean fullyBooked){
        LocalDate testDate = LocalDate.now().withMonth(1).withDayOfMonth(1);
        if (!LocalDate.now().isBefore(testDate)) {
            testDate = testDate.plusYears(1);
        }

        LocalDate startOfMonth = testDate.withDayOfMonth(1);
        LocalDate endOfMonth = testDate.withDayOfMonth(testDate.lengthOfMonth());

        List<BookableDate> fakeBookableDates = List.of(BookableDate.builder()
                .date(testDate)
                .touchUp(touchUp)
                .dropIn(dropIn)
                .fullyBooked(fullyBooked)
                .build());

        Mockito.when(bookableDateService.getBookableDatesBetweenTwoDates(startOfMonth, endOfMonth))
                .thenReturn(fakeBookableDates);

        Mockito.when(bookableDateService.convertListOfBookableDatesToBookableDateForCalendarDto(fakeBookableDates))
                .thenReturn(List.of(BookableDateForCalendarDto.builder()
                        .date(testDate)
                        .touchUp(touchUp)
                        .dropIn(dropIn)
                        .fullyBooked(fullyBooked)
                        .build()));

        return testDate;
    }
}