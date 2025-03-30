package com.example.demo.controller.customer;

import com.example.demo.model.BookableDate;
import com.example.demo.model.dtos.bookabledatedtos.BookableDateForCalendarDto;
import com.example.demo.services.BookableDateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CalendarController {
    BookableDateService bookableDateService;

    public CalendarController(BookableDateService bookableDateService){
        this.bookableDateService = bookableDateService;
    }

    @GetMapping("/calendar")
    public String getCalendar(@RequestParam(name = "year", required = false) Integer year,
                              @RequestParam(name = "month", required = false) Integer month,
                              Model model) {
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

        return "customer/customer-calendar";
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
