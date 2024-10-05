package com.example.demo.controller;

import com.example.demo.model.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.services.BookingService;
import com.example.demo.services.CustomerService;
import com.example.demo.services.TattooImageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookingService bookingService;
    private final CustomerService customerService;
    private final TattooImageService tattooImageService;

    public AdminController(BookingService bookingService, CustomerService customerService, TattooImageService tattooImageService) {
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.tattooImageService = tattooImageService;
    }

    @RequestMapping("/book-tattoo")
    @PreAuthorize("hasAuthority('Admin')")
    public String bookTattoo(){
        return "book-tattoo";
    }

    @RequestMapping("/bookings")
    @PreAuthorize("hasAuthority('Admin')")
    public String displayBookingsForAdmin(Model model){
        model.addAttribute("upcomingBookings",
                bookingService.getBookingsFromThisDateToFourWeeksForward().stream()
                        .map(bookingService::convertBookingToBookingCustomerDepositTimeDto)
                        .toList());
        return "bookings";
    }

    @RequestMapping("/adjust-booking")
    @PreAuthorize("hasAuthority('Admin')")
    public String adjustBooking(BookingCustomerDepositTimeDto booking, Model model){
        model.addAttribute("booking",
                bookingService.convertBookingCustomerDepositTimeDtoToBookingWithoutIdDto(booking));
        return "adjust-booking";
    }

    @RequestMapping("/set-front-page-images")
    @PreAuthorize("hasAuthority('Admin')")
    public String setFrontPageImage(Model model){
        model.addAttribute("imagesOnFrontPage", tattooImageService.getAllFrontPageImages());
        model.addAttribute("imagesNotOnFrontPage", tattooImageService.getAllNonFrontPageImages());
        return "select-front-page-images";
    }

    @RequestMapping("/customer")
    @PreAuthorize("hasAuthority('Admin')")
    public String customerInformation(@RequestParam String instagram, @RequestParam String phone, Model model){
        model.addAttribute("customer", customerService.findCustomerByPhoneOrInstagram(instagram, phone));
        return "customer";
    }
}
