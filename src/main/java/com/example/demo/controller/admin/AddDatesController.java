package com.example.demo.controller.admin;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.services.BookableDateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
        for(BookableDate b: bookableDateService.getAllCurrentlyAvailableBookableDates()){
            System.out.println(b.getDate() + " | dropin: " + b.isDropIn() + " | touchup: " + b.isTouchUp());
            for(BookableHour bh: b.getBookableHours()){
                System.out.println(bh.getHour());
            }
        }
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
    public String saveDates(@RequestParam Map<String, String> datesMap, Model model) {
        List<BookableDate> datesToSave = createDatesWithTypeAndHoursFromDateInformationMap(datesMap);
        bookableDateService.saveAllBookableDatesAndAssociatedHours(datesToSave);
        model.addAttribute("datesAdded", "dates added successfully!");

        return "admin/admin-landing-page";
    }


    private static List<BookableDate> createDatesWithTypeAndHoursFromDateInformationMap(Map<String, String> datesMap) {
        List<BookableDate> datesWithTypeAndHours = new ArrayList<>();
        BookableDate bookableDate = new BookableDate();

        for(String dateInformationKey: datesMap.keySet()){
            if(dateInformationKey.startsWith("type_")){
                bookableDate = BookableDate.builder()
                        .bookableHours(new ArrayList<>())
                        .date(LocalDate.parse(dateInformationKey.substring(dateInformationKey.indexOf("_") + 1)))
                        .build();
                String type = datesMap.get(dateInformationKey);
                bookableDate.setDropIn(type.equals("dropin"));
                bookableDate.setTouchUp(type.equals("touchup"));
                datesWithTypeAndHours.add(bookableDate);
            }else if(dateInformationKey.startsWith("dates")){
                bookableDate.getBookableHours().add(BookableHour.builder()
                        .hour(LocalTime.parse(dateInformationKey.substring(dateInformationKey.indexOf("]") + 1)))
                        .date(bookableDate)
                        .build());
            }
        }

        return datesWithTypeAndHours;
    }

}
