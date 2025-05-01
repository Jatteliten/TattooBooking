package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dto.bookabledate.BookableDateCalendarDto;
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

        List<BookableDateCalendarDto> days = createDaysInMonthFromSelectedDate(selectedDate);

        model.addAttribute("days", days);
        model.addAttribute("month", selectedDate.getMonthValue());
        model.addAttribute("year", selectedDate.getYear());
        model.addAttribute("prevMonth", selectedDate.minusMonths(1).getMonthValue());
        model.addAttribute("prevYear", selectedDate.minusMonths(1).getYear());
        model.addAttribute("nextMonth", selectedDate.plusMonths(1).getMonthValue());
        model.addAttribute("nextYear", selectedDate.plusMonths(1).getYear());
    }

    public List<BookableDateCalendarDto> createDaysInMonthFromSelectedDate(LocalDate selectedDate) {
        LocalDate firstDayOfMonth = selectedDate.withDayOfMonth(1);
        LocalDate firstDayOfCalendar = firstDayOfMonth.with(DayOfWeek.MONDAY);
        LocalDate lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());
        LocalDate lastDayOfCalendar = lastDayOfMonth.with(DayOfWeek.SUNDAY);
        List<BookableDateCalendarDto> days = new ArrayList<>();
        LocalDate date = firstDayOfCalendar;

        Map<LocalDate, BookableDateCalendarDto> dateToBookableDateForCalendarDto =
                bookableDateService.convertListOfBookableDatesToBookableDateCalendarDto(
                                bookableDateService.getBookableDatesBetweenTwoDates(
                                        firstDayOfMonth, lastDayOfMonth))
                        .stream()
                        .collect(Collectors.toMap(BookableDateCalendarDto::getDate, dto -> dto));

        while (!date.isAfter(lastDayOfCalendar)) {
            boolean isCurrentMonth = date.getMonth() == selectedDate.getMonth();
            BookableDateCalendarDto bookableDate = dateToBookableDateForCalendarDto.get(date);
            if(date.isAfter(LocalDate.now().minusDays(1)) && bookableDate != null){
                bookableDate.setCurrentMonth(isCurrentMonth);
                if(date.isBefore(LocalDate.now()) && !bookableDate.isDropIn()){
                    bookableDate.setFullyBooked(true);
                    bookableDate.getHours().clear();
                }
                days.add(bookableDate);
            }else{
                BookableDateCalendarDto bookableDateCalendarDto = BookableDateCalendarDto.builder()
                        .currentMonth(isCurrentMonth)
                        .bookable(false)
                        .date(date)
                        .build();
                days.add(bookableDateCalendarDto);
            }
            date = date.plusDays(1);
        }
        return days;
    }
}
