package com.example.demo.controller.admin;

import com.example.demo.dtos.bookabledatedtos.DateEntry;
import com.example.demo.dtos.bookabledatedtos.DateForm;
import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.services.BookableDateService;
import com.example.demo.services.BookingService;
import com.example.demo.services.CalendarService;
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
@RequestMapping("/admin/add-dates")
@PreAuthorize("hasAuthority('Admin')")
public class BookableDatesController {
    private final BookableDateService bookableDateService;
    private final CalendarService calendarService;
    private final BookingService bookingService;

    public BookableDatesController(BookableDateService bookableDateService, CalendarService calendarService, BookingService bookingService){
        this.bookableDateService = bookableDateService;
        this.calendarService = calendarService;
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String addDates(@RequestParam(name = "year", required = false) Integer year,
                           @RequestParam(name = "month", required = false) Integer month,
                           Model model){
        calendarService.createCalendarModel(model, year, month);
        return "admin/add-available-dates";
    }

    @PostMapping("/remove-all-bookable-dates")
    public String removeAllBookableDates(@RequestParam(name = "year", required = false) Integer year,
                                         @RequestParam(name = "month", required = false) Integer month,
                                         Model model){
        bookableDateService.deleteAllBookableDates();
        model.addAttribute("errorMessage", "All bookable dates deleted");
        calendarService.createCalendarModel(model, year, month);
        return "admin/add-available-dates";
    }

    @GetMapping("/confirm-dates")
    public String confirmDatesToAdd(@RequestParam List<String> time, @RequestParam String fromDate,
                                    @RequestParam String toDate, Model model){
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        if(!from.isBefore(to) && !from.isEqual(to)){
            model.addAttribute("errorMessage", "From date must be before To date");
            calendarService.createCalendarModel(model, null, null);

            return "admin/add-available-dates";
        }

        List<LocalDate> availableDates = new ArrayList<>();
        List<BookableDate> alreadyExistingBookableDateList =
                bookableDateService.findBookableDatesBetweenTwoGivenDates(from, to);

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
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
                    .fullyBooked(false)
                    .build();

            if(entry.getType() != null){
                bookableDate.setTouchUp(entry.getType().equals("touchup"));
                bookableDate.setDropIn(entry.getType().equals("dropin"));
            }else{
                bookableDate.setTouchUp(false);
                bookableDate.setDropIn(false);
            }

            if(!bookableDate.isDropIn() && entry.getHours() != null){
                bookableDate.setBookableHours(entry.getHours().stream()
                        .map(hour -> BookableHour.builder()
                                .hour(hour)
                                .booked(false)
                                .build())
                        .toList());
            }else if(bookableDate.isDropIn()){
                bookableDate.setBookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.of(11, 0))
                        .build()));
            }

            if(bookableDate.getBookableHours() != null){
                bookableDatesToSaveList.add(bookableDate);
            }
        }

        bookableDateService.saveListOfBookableDates(bookableDatesToSaveList);

        model.addAttribute("landingPageSingleLineMessage", bookableDatesToSaveList.size() + " dates added!");
        return "admin/admin-landing-page";
    }

    @GetMapping("/change-bookable-date")
    public String changeBookableDate(@RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);

        if(bookableDate != null){
            model.addAttribute("bookableDate", bookableDate);
        }else{
            model.addAttribute("bookableDate", BookableDate.builder().date(date).build());
        }

        model.addAttribute("bookings", bookingService.getBookingsByDate(date));
        return "admin/change-bookable-date";
    }

    @PostMapping("/close-date")
    public String makeDateUnavailable(@RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        bookableDateService.setBookableDateAndHoursToUnavailable(bookableDate);

        model.addAttribute("positiveFeedback", "Date " + bookableDate.getDate() + " set as unavailable.");
        model.addAttribute("bookableDate", bookableDate);
        model.addAttribute("bookings", bookingService.getBookingsByDate(date));
        return "admin/change-bookable-date";
    }

    @PostMapping("/open-date")
    public String makeDateAvailable(@RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        bookableDateService.setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(bookableDate, bookingService.getBookingsByDate(date));
        if(bookableDate.isFullyBooked()){
            model.addAttribute("negativeFeedback", "All available hours for " +
                    bookableDate.getDate() + " are booked. Cannot set as available");
        }else{
            model.addAttribute("positiveFeedback", "Date " + bookableDate.getDate() + " set as available.");
        }

        model.addAttribute("bookableDate", bookableDate);
        model.addAttribute("bookings", bookingService.getBookingsByDate(date));
        return "admin/change-bookable-date";
    }

    @PostMapping("/remove-hour")
    public String setHourToBooked(@RequestParam LocalTime hour, @RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        BookableHour bookableHour = bookableDate.getBookableHours().stream()
                .filter(bh -> bh.getHour().equals(hour)).toList().getFirst();

        if(bookableHour != null){
            bookableHour.setBooked(true);
            model.addAttribute("positiveFeedback", hour + " set as non bookable.");
        }else{
            model.addAttribute("negativeFeedback", "Cannot change hour.");
        }

        bookableDateService.saveBookableDate(bookableDate);

        model.addAttribute("bookableDate", bookableDate);
        model.addAttribute("bookings", bookingService.getBookingsByDate(date));

        return "admin/change-bookable-date";
    }

    @PostMapping("/add-hour")
    public String unBookHour(@RequestParam LocalTime hour, @RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        BookableHour bookableHour = bookableDate.getBookableHours().stream()
                .filter(bh -> bh.getHour().equals(hour)).toList().getFirst();

        if(bookableHour != null){
            if(bookingService.getBookingsByDate(date).stream().filter(booking ->
                    booking.getDate().toLocalTime().isBefore(hour) &&
                            booking.getEndTime().toLocalTime().isAfter(hour)).toList().isEmpty()){
                bookableHour.setBooked(false);
                model.addAttribute("positiveFeedback", hour + " set as book-able.");
            }else{
                model.addAttribute("negativeFeedback", "Hour is booked");
            }
        }else{
            model.addAttribute("negativeFeedback", "Cannot change hour.");
        }

        bookableDateService.saveBookableDate(bookableDate);

        model.addAttribute("bookableDate", bookableDate);
        model.addAttribute("bookings", bookingService.getBookingsByDate(date));

        return "admin/change-bookable-date";
    }

}
