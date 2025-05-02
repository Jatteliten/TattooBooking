package com.example.tattooplatform.controller.admin;

import com.example.tattooplatform.dto.bookabledate.DateForm;
import com.example.tattooplatform.model.BookableDate;
import com.example.tattooplatform.model.BookableHour;
import com.example.tattooplatform.model.Booking;
import com.example.tattooplatform.services.BookableDateService;
import com.example.tattooplatform.services.BookingService;
import com.example.tattooplatform.services.CalendarService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin/add-dates")
@PreAuthorize("hasAuthority('Admin')")
public class BookableDateController {
    private final BookableDateService bookableDateService;
    private final CalendarService calendarService;
    private final BookingService bookingService;

    public BookableDateController(BookableDateService bookableDateService, CalendarService calendarService, BookingService bookingService){
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

    @GetMapping("/confirm-dates")
    public String confirmDatesToAdd(@RequestParam List<String> time, @RequestParam String fromDate,
                                    @RequestParam String toDate, Model model){
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        if(from.isAfter(to)){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "From date must be before To date");
            calendarService.createCalendarModel(model, null, null);

            return "admin/add-available-dates";
        }

        model.addAttribute("dates", bookableDateService.getAvailableDatesBetweenTwoDates(from, to));
        model.addAttribute("times", time);
        return "admin/confirm-dates";
    }

    @PostMapping("/save-dates")
    public String saveDates(@ModelAttribute DateForm dateForm, Model model) {
        List<BookableDate> bookableDates = bookableDateService.createBookableDatesFromDateForm(dateForm);

        for(BookableDate bookableDate: bookableDates){
            bookableDateService.setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(bookableDate,
                    bookingService.sortBookingsByStartDateAndTime(
                            bookingService.getBookingsByDate(bookableDate.getDate())));
        }
        bookableDateService.saveListOfBookableDates(bookableDates);

        model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(),
                bookableDates.size() + " date(s) added!");
        model.addAttribute("bookableDates", bookableDates);
        return "admin/admin-landing-page";
    }

    @GetMapping("/change-bookable-date")
    public String changeBookableDate(@RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);

        if(bookableDate == null){
            bookableDate = BookableDate.builder().date(date).build();
        }

        return addBookableDateAndBookingsToModelAndReturnBookableDateView(bookableDate, date, model);
    }

    @PostMapping("/close-date")
    public String makeDateUnavailable(@RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        bookableDateService.setBookableDateAndHoursToUnavailable(bookableDate);

        model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Date " + bookableDate.getDate() + " set as unavailable.");
        return addBookableDateAndBookingsToModelAndReturnBookableDateView(bookableDate, date, model);
    }

    @PostMapping("/open-date")
    public String makeDateAvailable(@RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        bookableDateService.setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(
                bookableDate, bookingService.sortBookingsByStartDateAndTime(bookingService.getBookingsByDate(date)));
        bookableDateService.saveBookableDate(bookableDate);

        if(bookableDate.isFullyBooked()){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "All available hours for " +
                    bookableDate.getDate() + " are booked. Cannot set as available");
        }else{
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Date " + bookableDate.getDate() + " set as available.");
        }

        return addBookableDateAndBookingsToModelAndReturnBookableDateView(bookableDate, date, model);
    }

    @PostMapping("/make-hour-unavailable")
    public String setHourToBooked(@RequestParam LocalTime hour, @RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        BookableHour bookableHour = bookableDate.getBookableHours().stream()
                .filter(bh -> bh.getHour().equals(hour))
                .findFirst()
                .orElse(null);

        if(bookableHour != null){
            bookableHour.setBooked(true);
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), hour + " set as unavailable.");
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Cannot change hour.");
        }

        bookableDateService.saveBookableDate(bookableDate);

        return addBookableDateAndBookingsToModelAndReturnBookableDateView(bookableDate, date, model);
    }

    @PostMapping("/make-hour-available")
    public String unBookHour(@RequestParam LocalTime hour, @RequestParam LocalDate date, Model model){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        BookableHour bookableHour = bookableDate.getBookableHours().stream()
                .filter(bh -> bh.getHour().equals(hour))
                .findFirst()
                .orElse(null);

        if(bookableHour != null){
            if(bookableDateService.checkIfHourIsAvailable(
                    hour, bookingService.sortBookingsByStartDateAndTime(bookingService.getBookingsByDate(date)))){
                bookableDateService.setBookableDateAndBookableHourToAvailable(bookableDate, bookableHour);

                model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), hour + " set as available.");
            }else{
                model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), hour + " is booked");
            }
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Cannot change hour.");
        }

        return addBookableDateAndBookingsToModelAndReturnBookableDateView(bookableDate, date, model);
    }

    @PostMapping("/add-new-hour")
    public String addNewHourToBookableDate(@RequestParam LocalTime hour, @RequestParam LocalDate date, Model model){
        List<Booking> bookings = bookingService.sortBookingsByStartDateAndTime(bookingService.getBookingsByDate(date));
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);

        if(bookableDateService.checkIfHourIsAvailable(hour, bookings)){
            if(!bookableDateService.checkIfBookableHourExistsAtBookableDate(bookableDate, hour)){
                bookableDateService.addHourToBookableDate(bookableDate, hour);

                model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), hour + " Added to date.");
            }else{
                model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), hour + " already exists as a bookable hour.");
            }
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), hour + " is already booked.");
        }

        return addBookableDateAndBookingsToModelAndReturnBookableDateView(bookableDate, date, model);
    }

    private String addBookableDateAndBookingsToModelAndReturnBookableDateView(BookableDate bookableDate, LocalDate date, Model model){
        model.addAttribute("bookableDate", bookableDate);
        model.addAttribute("bookings", bookingService.sortBookingsByStartDateAndTime(
                bookingService.sortBookingsByStartDateAndTime(bookingService.getBookingsByDate(date))));
        return "admin/change-bookable-date";
    }

}