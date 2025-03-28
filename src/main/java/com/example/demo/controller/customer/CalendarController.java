package com.example.demo.controller.customer;

import com.example.demo.model.BookableDate;
import com.example.demo.services.BookableDateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

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

        List<CalendarDay> days = createDaysInMonthFromSelectedDate(selectedDate);

        model.addAttribute("days", days);
        model.addAttribute("month", selectedDate.getMonthValue());
        model.addAttribute("year", selectedDate.getYear());
        model.addAttribute("prevMonth", selectedDate.minusMonths(1).getMonthValue());
        model.addAttribute("prevYear", selectedDate.minusMonths(1).getYear());
        model.addAttribute("nextMonth", selectedDate.plusMonths(1).getMonthValue());
        model.addAttribute("nextYear", selectedDate.plusMonths(1).getYear());

        return "customer/customer-calendar";
    }

    private List<CalendarDay> createDaysInMonthFromSelectedDate(LocalDate selectedDate) {
        LocalDate firstDayOfMonth = selectedDate.withDayOfMonth(1);
        LocalDate firstDayOfCalendar = firstDayOfMonth.with(DayOfWeek.MONDAY);
        LocalDate lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());
        LocalDate lastDayOfCalendar = lastDayOfMonth.with(DayOfWeek.SUNDAY);

        List<CalendarDay> days = new ArrayList<>();
        LocalDate date = firstDayOfCalendar;
        List<BookableDate> bookableDateList = bookableDateService.getAllCurrentlyAvailableBookableDates();
        while (!date.isAfter(lastDayOfCalendar)) {
            boolean isCurrentMonth = date.getMonth() == selectedDate.getMonth();
            BookableDate bookableDate = bookableDateService.getBookableDateFromBookableDateListByDate(bookableDateList, date);
            if(bookableDate != null){
                days.add(new CalendarDay(date, isCurrentMonth, false));
            }else{
                days.add(new CalendarDay(date, isCurrentMonth, true));
            }
            date = date.plusDays(1);
        }
        return days;
    }

    public record CalendarDay(LocalDate date, boolean currentMonth, boolean fullyBooked) { }
}
