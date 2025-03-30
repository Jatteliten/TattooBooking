package com.example.demo.controller.admin;

import com.example.demo.dtos.calendardtos.DateEntry;
import com.example.demo.dtos.calendardtos.DateForm;
import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.services.BookableDateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/remove-all-bookable-dates")
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

        if(!from.isBefore(to) && !from.isEqual(to)){
            model.addAttribute("errorMessage", "From date must be before To date");
            return "admin/add-available-dates";
        }

        List<LocalDate> availableDates = new ArrayList<>();
        List<BookableDate> alreadyExistingBookableDateList =
                bookableDateService.findBookableDatesBetweenTwoGivenDates(
                        LocalDate.parse(fromDate), LocalDate.parse(toDate));

        for (LocalDate date = from; !date.isAfter(to);
             date = date.plusDays(1)) {
            LocalDate currentDateInIteration = date;

            if (date.getDayOfWeek() != DayOfWeek.SUNDAY && alreadyExistingBookableDateList.stream()
                    .filter(bookableDate -> bookableDate.getDate().equals(currentDateInIteration))
                    .toList()
                    .isEmpty()) {
                availableDates.add(date);
            }
        }

        model.addAttribute("dates", availableDates);
        model.addAttribute("times", time);
        return "admin/confirm-dates";
    }

    @PostMapping("/save-dates")
    public String saveDates(@ModelAttribute DateForm dateForm, Model model) {
        List<BookableDate> bookableDatesToSaveList = new ArrayList<>();
        for (DateEntry entry : dateForm.getDateList()) {
            BookableDate bookableDate = BookableDate.builder()
                    .date(entry.getDate())
                    .touchUp(entry.getType().equals("touchup"))
                    .dropIn(entry.getType().equals("dropin"))
                    .fullyBooked(false)
                    .build();

            if(!bookableDate.isDropIn()){
                bookableDate.setBookableHours(entry.getHours().stream()
                        .map(hour -> BookableHour.builder()
                                .hour(hour)
                                .booked(false)
                                .build())
                        .toList());
            }else{
                bookableDate.setBookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.of(12, 0))
                        .build()));
            }

            bookableDatesToSaveList.add(bookableDate);
        }

        bookableDateService.saveListOfBookableDates(bookableDatesToSaveList);

        model.addAttribute("datesAdded", bookableDatesToSaveList.size() + " dates added!");
        return "admin/admin-landing-page";
    }

}
