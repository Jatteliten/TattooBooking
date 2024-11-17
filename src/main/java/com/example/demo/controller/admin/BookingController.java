package com.example.demo.controller.admin;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.model.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.services.BookableDateService;
import com.example.demo.services.BookableHourService;
import com.example.demo.services.BookingService;
import com.example.demo.services.CustomerService;
import com.example.demo.util.calendar.CalendarService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/booking")
@PreAuthorize("hasAuthority('Admin')")
public class BookingController {
    private final CalendarService calendarService;
    private final BookingService bookingService;
    private final CustomerService customerService;
    private final BookableDateService bookableDateService;
    private final BookableHourService bookableHourService;

    public BookingController(CalendarService calendarService, BookingService bookingService,
                             CustomerService customerService, BookableDateService bookableDateService, BookableHourService bookableHourService){
        this.calendarService = calendarService;
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.bookableDateService = bookableDateService;
        this.bookableHourService = bookableHourService;
    }


    @RequestMapping("/book-tattoo")
    public String bookTattoo(Model model){
        model.addAttribute("weeks", calendarService.getNextTwentyEightDates());
        return "book-tattoo";
    }

    @RequestMapping("/bookings")
    public String displayBookingsForAdmin(Model model){
        model.addAttribute("upcomingBookings",
                bookingService.getBookingsFromTodayToFourWeeksForward().stream()
                        .map(bookingService::convertBookingToBookingCustomerDepositTimeDto)
                        .toList());
        return "bookings";
    }

    @RequestMapping("/adjust-booking")
    public String adjustBooking(BookingCustomerDepositTimeDto booking, Model model){
        model.addAttribute("booking",
                bookingService.convertBookingCustomerDepositTimeDtoToBookingWithoutIdDto(booking));
        return "adjust-booking";
    }

    @RequestMapping("/book-tattoo-at-date")
    public String bookTattooWithGivenDate(@RequestParam LocalDate date, Model model){
        model.addAttribute("selectedDate", date);
        BookableDate bookableDate = bookableDateService.findBookableDateByDate(date);
        if(bookableDate != null) {
            model.addAttribute("bookableHours", bookableDateService.findBookableDateByDate(date).getBookableHours());
        }
        return "book-tattoo-with-date";
    }

    @GetMapping("/search-customer")
    public String searchCustomer(@RequestParam String searchInput, @RequestParam LocalDate date, Model model){
        model.addAttribute("selectedDate", date);
        Customer customer = customerService.findCustomerByPhoneInstagramOrEmail(searchInput);

        if(customer == null){
            model.addAttribute("searchResult", "No customer found");
        }else {
            model.addAttribute("searchResult", customerService.findCustomerByPhoneInstagramOrEmail(searchInput));
        }

        BookableDate bookableDate = bookableDateService.findBookableDateByDate(date);
        if(bookableDate != null) {
            model.addAttribute("bookableHours", bookableDateService.findBookableDateByDate(date).getBookableHours());
        }
        return "book-tattoo-with-date";
    }

    @PostMapping("/create-customer")
    public String createNewCustomer(@RequestParam LocalDate date, Model model, Customer customer){
        customerService.saveCustomer(customer);
        model.addAttribute("selectedDate", date);
        model.addAttribute("searchResult", customerService.findCustomerIfAtLeastOneContactMethodExists(customer));

        return "book-tattoo-with-date";
    }

    @PostMapping("/book-tattoo-with-customer")
    public String bookSessionWithCustomer(@RequestParam LocalDate date, @RequestParam LocalTime startTime,
                                          @RequestParam LocalTime endTime, @RequestParam String customerEmail,
                                          @RequestParam String customerInstagram, @RequestParam String customerPhone,
                                          Model model){
        Customer customerToBook = customerService.findCustomerIfAtLeastOneContactMethodExists(
                Customer.builder()
                        .email(customerEmail)
                        .phone(customerPhone)
                        .instagram(customerInstagram)
                        .build());

        if(customerToBook != null) {
            Booking booking = Booking.builder()
                    .date(date.atTime(startTime))
                    .customer(customerService.findCustomerIfAtLeastOneContactMethodExists(customerToBook))
                    .build();
            bookingService.saveBooking(booking);
        }

        BookableDate bookableDate = bookableDateService.findBookableDateByDate(date);
        if(bookableDate != null){
            for(BookableHour bookableHour: bookableDate.getBookableHours()){
                if(bookableHour.isBooked()){
                    model.addAttribute("doubleBookError", "Can't book at already booked times");
                    return "admin-landing-page";
                }
                if(bookableHour.getHour().isAfter(startTime.minusMinutes(1))
                        && bookableHour.getHour().isBefore(endTime)){
                    bookableHour.setBooked(true);
                }
            }
            bookableHourService.saveAllBookableHours(bookableDate.getBookableHours());
        }


        model.addAttribute("bookingAdded", customerToBook.getName()
                + " booked at " + startTime.toString()
                + " on " + date.toString());
        return "admin-landing-page";
    }

}
