package com.example.demo.controller.admin;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.services.BookableDateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/add-dates")
@PreAuthorize("hasAuthority('Admin')")
public class AddDatesController {

    private final BookableDateService bookableDateService;
    public AddDatesController(BookableDateService bookableDateService){
        this.bookableDateService = bookableDateService;
    }
    @GetMapping("/")
    public String addDates(){
        return "admin/add-available-dates";
    }

    @DeleteMapping("/removeAllBookableDates")
    public String removeAllBookableDates(Model model){
        bookableDateService.deleteAllBookableDates();
        model.addAttribute("errorMessage", "All bookable dates deleted");
        return "admin/add-available-dates";
    }

    @GetMapping("/confirm-dates")
    public String confirmDatesToAdd(@RequestParam List<String> time, @RequestParam String fromDate,
                                    @RequestParam String toDate, Model model){
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        if(!from.isBefore(to)){
            model.addAttribute("errorMessage", "From date must be before To date");
            return "admin/add-available-dates";
        }

        List<LocalDate> availableDates = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(to);
             date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                availableDates.add(date);
            }
        }
        model.addAttribute("dates", availableDates);
        model.addAttribute("times", time);
        return "admin/confirm-dates";
    }

    @PostMapping("/save-dates")
    public String saveDates(@RequestParam List<String> dates, Model model) {
        Map<String, List<String>> datesWithHours = createDatesWithHoursMap(dates);

        List<BookableDate> datesToSave = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : datesWithHours.entrySet()) {
            List<BookableHour> hoursToSave = new ArrayList<>();
            for(String s: entry.getValue()){
                hoursToSave.add(BookableHour.builder()
                        .hour(LocalTime.parse(s))
                        .booked(false)
                        .build());
            }
            BookableDate dateToSave = BookableDate.builder()
                    .date(LocalDate.parse(entry.getKey()))
                    .bookableHours(hoursToSave)
                    .fullyBooked(false)
                    .build();
            datesToSave.add(dateToSave);
        }

        bookableDateService.saveAllBookableDatesAndAssociatedHours(datesToSave);

        model.addAttribute("datesAdded", datesToSave.size() + " dates added");
        return "admin/admin-landing-page";
    }

    private static Map<String, List<String>> createDatesWithHoursMap(List<String> dates) {
        String oldValue = "nothing";
        Map<String, List<String>> datesWithHours = new HashMap<>();

        for(String s: dates){
            String date = s.split("=")[0];
            String time = s.split("=")[1];
            if(date.equals(oldValue)){
                List<String> times = datesWithHours.get(date);
                times.add(time);
            }else{
                List<String> newTimesList = new ArrayList<>();
                newTimesList.add(time);
                datesWithHours.put(date, newTimesList);
            }
            oldValue = date;
        }
        return datesWithHours;
    }

}
