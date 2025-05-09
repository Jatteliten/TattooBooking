package com.example.tattooplatform.controller.customer;

import com.example.tattooplatform.services.CalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Controller
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService){
        this.calendarService = calendarService;
    }

    @GetMapping("/calendar")
    public String getCalendar(@RequestParam(name = "year", required = false) Integer year,
                              @RequestParam(name = "month", required = false) Integer month,
                              Model model) {
        int todayYear = LocalDate.now().getYear();

        if (year != null && (year < todayYear - 2 || year > todayYear + 2)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid year");
        }

        if (month != null && (month < 1 || month > 12)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid month");
        }

        model.addAttribute("currentYear", todayYear);
        model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
        calendarService.createCalendarModel(model, year, month);

        return "customer/customer-calendar";
    }


}
