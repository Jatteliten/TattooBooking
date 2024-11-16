package com.example.demo.controller.admin;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/booking")
@PreAuthorize("hasAuthority('Admin')")
public class BookingController {

    CalendarService calendarService;
    BookingService bookingService;
    CustomerService customerService;
    BookableHourService bookableHourService;
    BookableDateService bookableDateService;
    public BookingController(CalendarService calendarService, BookingService bookingService,
                             CustomerService customerService, BookableHourService bookableHourService,
                             BookableDateService bookableDateService){
        this.calendarService = calendarService;
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.bookableHourService = bookableHourService;
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
        model.addAttribute("searchResult", findCustomerByAllParameters(customer));

        return "book-tattoo-with-date";
    }

    @PostMapping("/book-tattoo-with-customer")
    public String bookSessionWithCustomer(@RequestParam LocalDate date, @RequestParam LocalTime time,
                                          @RequestParam String customerEmail, @RequestParam String customerInstagram,
                                          @RequestParam String customerPhone, Model model){
        Customer customerToBook = findCustomerByAllParameters(
                Customer.builder()
                        .email(customerEmail)
                        .phone(customerPhone)
                        .instagram(customerInstagram)
                        .build());

        Booking booking = Booking.builder()
                .date(date.atTime(time))
                .customer(findCustomerByAllParameters(customerToBook))
                .build();
        bookingService.saveBooking(booking);

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


    //for (LocalDate date = LocalDate.parse(fromDate); !date.isAfter(LocalDate.parse(toDate));
    //     date = date.plusDays(1)) {
    //    AvailableDate availableDateToSave = new AvailableDate();
    //    availableDateToSave.setDate(date);
    //    List<BookableHour> hours = new ArrayList<>();
    //    for(String hour: time){
    //        LocalTime tempTime = LocalTime.parse(hour);
    //        hours.add(bookableHourService.findBookableHourOrCreateBookableHourIfItDoesNotAlreadyExist(tempTime));
    //    }
    //}

    private Customer findCustomerByAllParameters(Customer customer){
        if(customer.getEmail() != null){
            return customerService.findCustomerByPhoneInstagramOrEmail(customer.getEmail());
        }else if(customer.getInstagram() != null){
            return  customerService.findCustomerByPhoneInstagramOrEmail(customer.getInstagram());
        }else if(customer.getPhone() != null){
            return customerService.findCustomerByPhoneInstagramOrEmail(customer.getPhone());
        }
        return null;
    }
}
