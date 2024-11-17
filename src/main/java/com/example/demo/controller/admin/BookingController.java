package com.example.demo.controller.admin;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.model.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.services.BookableDateService;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/booking")
@PreAuthorize("hasAuthority('Admin')")
public class BookingController {
    private final CalendarService calendarService;
    private final BookingService bookingService;
    private final CustomerService customerService;
    private final BookableDateService bookableDateService;
    public BookingController(CalendarService calendarService, BookingService bookingService,
                             CustomerService customerService, BookableDateService bookableDateService){
        this.calendarService = calendarService;
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.bookableDateService = bookableDateService;
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
    public String bookSessionWithCustomer(@RequestParam LocalDate date, @RequestParam LocalTime time,
                                          @RequestParam String customerEmail, @RequestParam String customerInstagram,
                                          @RequestParam String customerPhone, Model model){
        Customer customerToBook = customerService.findCustomerIfAtLeastOneContactMethodExists(
                Customer.builder()
                        .email(customerEmail)
                        .phone(customerPhone)
                        .instagram(customerInstagram)
                        .build());

        if(customerToBook != null) {
            Booking booking = Booking.builder()
                    .date(date.atTime(time))
                    .customer(customerService.findCustomerIfAtLeastOneContactMethodExists(customerToBook))
                    .build();
            bookingService.saveBooking(booking);
        }

        for(Booking b: bookingService.getAllBookings()){
            System.out.println(b.getDate());
        }

        model.addAttribute("successCustomer", customerToBook);
        model.addAttribute("successDate", date);
        model.addAttribute("successTime", time);
        return "book-tattoo-with-date";
    }

    @RequestMapping("/add-dates")
    public String addDates(){
        return "add-available-dates";
    }

    @RequestMapping("/confirm-dates")
    public String confirmDatesToAdd(@RequestParam List<String> time, @RequestParam String fromDate,
                                    @RequestParam String toDate, Model model){
        List<LocalDate> availableDates = new ArrayList<>();

        for (LocalDate date = LocalDate.parse(fromDate); !date.isAfter(LocalDate.parse(toDate));
             date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                availableDates.add(date);
            }
        }
        model.addAttribute("dates", availableDates);
        model.addAttribute("times", time);
        return "confirm-dates";
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
        return "admin-landing-page";
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
