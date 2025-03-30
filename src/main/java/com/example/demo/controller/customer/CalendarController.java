package com.example.demo.controller.customer;

import com.example.demo.services.BookableDateService;
import com.example.demo.util.calendar.CalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {
    CalendarService calendarService;

    public CalendarController(CalendarService calendarService){
        this.calendarService = calendarService;
    }

    @GetMapping("/calendar")
    public String getCalendar(@RequestParam(name = "year", required = false) Integer year,
                              @RequestParam(name = "month", required = false) Integer month,
                              Model model) {
        calendarService.createCalendarModel(model, year, month);

        return "customer/customer-calendar";
    }


}
