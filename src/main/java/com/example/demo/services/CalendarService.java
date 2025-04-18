package com.example.demo.services;

import com.example.demo.dtos.bookabledatedtos.BookableDateForCalendarDto;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    private final BookableDateService bookableDateService;

    public CalendarService(BookableDateService bookableDateService){
        this.bookableDateService = bookableDateService;
    }

    public void createCalendarModel(Model model, Integer year, Integer month){
        LocalDate today = LocalDate.now();
        LocalDate selectedDate = LocalDate.of(
                (year != null) ? year : today.getYear(),
                (month != null) ? month : today.getMonthValue(),
                1
        );

        List<BookableDateForCalendarDto> days = createDaysInMonthFromSelectedDate(selectedDate);

        model.addAttribute("days", days);
        model.addAttribute("month", selectedDate.getMonthValue());
        model.addAttribute("year", selectedDate.getYear());
        model.addAttribute("prevMonth", selectedDate.minusMonths(1).getMonthValue());
        model.addAttribute("prevYear", selectedDate.minusMonths(1).getYear());
        model.addAttribute("nextMonth", selectedDate.plusMonths(1).getMonthValue());
        model.addAttribute("nextYear", selectedDate.plusMonths(1).getYear());
    }

    private List<BookableDateForCalendarDto> createDaysInMonthFromSelectedDate(LocalDate selectedDate) {
        LocalDate firstDayOfMonth = selectedDate.withDayOfMonth(1);
        LocalDate firstDayOfCalendar = firstDayOfMonth.with(DayOfWeek.MONDAY);
        LocalDate lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());
        LocalDate lastDayOfCalendar = lastDayOfMonth.with(DayOfWeek.SUNDAY);
        List<BookableDateForCalendarDto> days = new ArrayList<>();
        LocalDate date = firstDayOfCalendar;

        Map<LocalDate, BookableDateForCalendarDto> dateToBookableDateForCalendarDto =
                bookableDateService.convertListOfBookableDatesToBookableDateForCalendarDto(
                                bookableDateService.findBookableDatesBetweenTwoGivenDates(firstDayOfMonth, lastDayOfMonth))
                        .stream()
                        .collect(Collectors
                                .toMap(BookableDateForCalendarDto::getDate, dto -> dto));

        while (!date.isAfter(lastDayOfCalendar)) {
            boolean isCurrentMonth = date.getMonth() == selectedDate.getMonth();
            BookableDateForCalendarDto bookableDate = dateToBookableDateForCalendarDto.get(date);
            if(bookableDate != null){
                bookableDate.setCurrentMonth(isCurrentMonth);
                days.add(bookableDate);
            }else{
                days.add(BookableDateForCalendarDto.builder()
                        .currentMonth(isCurrentMonth)
                        .bookable(false)
                        .date(date)
                        .build());
            }
            date = date.plusDays(1);
        }
        return days;
    }
}
