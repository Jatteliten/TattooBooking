package com.example.demo.controller.admin;

import com.example.demo.model.BookableDate;
import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.services.BookableDateService;
import com.example.demo.services.BookableHourService;
import com.example.demo.services.BookingService;
import com.example.demo.services.CalendarService;
import com.example.demo.services.CustomerService;
import com.example.demo.services.ImageCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Controller
@RequestMapping("/admin/booking")
@PreAuthorize("hasAuthority('Admin')")
public class BookingController {
    private final CalendarService calendarService;
    private final BookingService bookingService;
    private final CustomerService customerService;
    private final BookableDateService bookableDateService;
    private final BookableHourService bookableHourService;
    private final ImageCategoryService imageCategoryService;

    public BookingController(CalendarService calendarService, BookingService bookingService, CustomerService customerService,
                             BookableDateService bookableDateService, BookableHourService bookableHourService, ImageCategoryService imageCategoryService){
        this.calendarService = calendarService;
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.bookableDateService = bookableDateService;
        this.bookableHourService = bookableHourService;
        this.imageCategoryService = imageCategoryService;
    }


    @GetMapping("/book-tattoo")
    public String bookTattoo(@RequestParam(name = "year", required = false) Integer year,
                             @RequestParam(name = "month", required = false) Integer month,
                             Model model){
        calendarService.createCalendarModel(model, year, month);

        return "admin/book-tattoo";
    }

    @GetMapping("/bookings")
    public String displayBookings(@RequestParam(required=false) LocalDate fromDate,
                                  @RequestParam(required=false) LocalDate toDate, Model model){
        List<Booking> bookings;

        if(fromDate != null && toDate != null){
            bookings = bookingService.getBookingsBetweenTwoGivenDates(
                    LocalDateTime.of(fromDate, LocalTime.of(0,0)),
                    LocalDateTime.of(toDate, LocalTime.of(23,59)));
        }else{
            bookings = bookingService.getBookingsByDate(LocalDate.now());
        }

        if(!bookings.isEmpty()){
            bookings.sort(Comparator.comparing(Booking::getDate));
            model.addAttribute("upcomingBookings", bookings);
        }else if(fromDate != null && toDate != null){
            model.addAttribute("errorMessage", "No bookings on given dates");
        }else{
            model.addAttribute("errorMessage", "No bookings today");
        }
        return "admin/bookings";
    }

    @GetMapping("/booking-information")
    public String viewBookingInformation(@RequestParam UUID id, Model model){
        model.addAttribute("booking", bookingService.getBookingById(id));
        model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        return "admin/booking-information";
    }

    @PostMapping("/update-booking")
    public String updateBookingInformation(@RequestParam String notes, @RequestParam UUID bookingId, Model model){
        Booking booking = bookingService.getBookingById(bookingId);
        booking.setNotes(notes);
        bookingService.saveBooking(booking);

        model.addAttribute("booking", booking);
        model.addAttribute("notesSaved", "Booking notes saved.");
        return "admin/booking-information";
    }

    @PostMapping("/remove-all-bookings")
    public String removeAllBookings(Model model){
        bookingService.deleteBookings(bookingService.getAllBookings());
        model.addAttribute("errorMessage", "All bookable dates deleted");
        return "admin/bookings";
    }

    @PostMapping("/cancel-appointment")
    public String removeBooking(@RequestParam UUID id, Model model){
        Booking booking = bookingService.getBookingById(id);
        bookingService.deleteBooking(booking);
        model.addAttribute("errorMessage", "Canceled " + booking.getCustomer().getName() + " at " + booking.getDate().toString());
        return "admin/bookings";
    }


    @GetMapping("/adjust-booking")
    public String adjustBooking(BookingCustomerDepositTimeDto booking, Model model){
        model.addAttribute("booking",
                bookingService.convertBookingCustomerDepositTimeDtoToBookingWithoutIdDto(booking));
        return "admin/adjust-booking";
    }

    @PostMapping("/upload-image")
    public String uploadBookingImage(@RequestParam("file") MultipartFile file,
                                     @RequestParam UUID bookingId,
                                     @RequestParam(value="categoryIds", required=false) List<UUID> categoryIds,
                                     Model model){
        if(categoryIds == null){
            model.addAttribute("failFeedback", "You must pick at least one category");
            model.addAttribute(bookingService.getBookingById(bookingId));
            model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        }else{
            try {
                Booking booking = bookingService.uploadTattooImage(bookingId, file, categoryIds);
                model.addAttribute("successFeedback", "Tattoo image uploaded!");
                model.addAttribute("booking", booking);
            } catch (IllegalArgumentException e) {
                model.addAttribute("failFeedback", e.getMessage());
            } catch (Exception e) {
                model.addAttribute("failFeedback", "Failed to upload image.");
            }
        }

        return "admin/booking-information";
    }

    @PostMapping("/delete-image")
    public String deleteTattooImage(@RequestParam UUID bookingId, Model model){
        try {
            Booking booking = bookingService.deleteTattooImage(bookingId);
            model.addAttribute("successFeedback", "Tattoo image deleted!");
            model.addAttribute("booking", booking);
            model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        } catch (Exception e) {
            model.addAttribute("failFeedback", "Failed to delete image.");
        }

        return "admin/booking-information";
    }

    @GetMapping("/book-tattoo-at-date")
    public String bookTattooWithGivenDate(@RequestParam LocalDate date, Model model){
        model.addAttribute("selectedDate", date);
        model.addAttribute("bookingsAtDate", bookingService.getBookingsByDate(date));
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        if(bookableDate != null) {
            model.addAttribute("bookableHours", bookableDate.getBookableHours());
        }
        return "admin/book-tattoo-with-date";
    }

    @GetMapping("/search-customer")
    public String searchCustomer(@RequestParam String searchInput, @RequestParam LocalDate date,
                                 Model model){
        model.addAttribute("selectedDate", date);
        model.addAttribute("bookingsAtDate", bookingService.getBookingsByDate(date));
        Customer customer = customerService.findCustomerByAnyField(searchInput);

        model.addAttribute("searchResult", Objects.requireNonNullElse(customer, "No customer found"));

        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        if(bookableDate != null) {
            model.addAttribute("bookableHours", bookableDate.getBookableHours());
        }
        return "admin/book-tattoo-with-date";
    }

    @PostMapping("/create-customer")
    public String createNewCustomer(@RequestParam LocalDate date,
                                    @RequestParam String name,
                                    @RequestParam(required = false) String phone,
                                    @RequestParam(required = false) String instagram,
                                    @RequestParam(required = false) String email, Model model) {
        Customer customer = Customer.builder()
                .name(name)
                .phone(phone)
                .instagram(instagram)
                .email(email)
                .build();

        Customer existingCustomer = customerService.findCustomerIfAtLeastOneContactMethodMatches(customer);
        if (existingCustomer == null) {
            customerService.saveCustomer(customer);
            model.addAttribute("searchResult", customer);
        } else {
            model.addAttribute("searchResult", existingCustomer);
        }

        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        if(bookableDate != null) {
            model.addAttribute("bookableHours", bookableDate.getBookableHours());
        }

        model.addAttribute("selectedDate", date);
        return "admin/book-tattoo-with-date";
    }

    @PostMapping("/book-tattoo-with-customer")
    public String bookSessionWithCustomer(@RequestParam LocalDate date, @RequestParam LocalTime startTime,
                                          @RequestParam LocalTime endTime, @RequestParam String customerEmail,
                                          @RequestParam String customerInstagram, @RequestParam String customerPhone,
                                          @RequestParam(required = false) Boolean touchUp,
                                          @RequestParam(required = false) Boolean depositPaid, Model model){
        LocalDateTime startDateAndTime = date.atTime(startTime);
        LocalDateTime endDateAndTime = date.atTime(endTime);

        if(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(startDateAndTime, endDateAndTime)){
            model.addAttribute("landingPageSingleLineMessage", "Can't book at already booked hours");
            return "admin/admin-landing-page";
        }

        Customer customerToBook = customerService.findCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .email(customerEmail)
                        .phone(customerPhone)
                        .instagram(customerInstagram)
                        .build());
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);

        if(customerToBook != null) {
            Booking booking = Booking.builder()
                    .date(startDateAndTime)
                    .endTime(endDateAndTime)
                    .customer(customerToBook)
                    .build();

            booking.setDepositPaid(depositPaid != null);

            if(touchUp != null && touchUp){
                booking.setTouchUp(true);
                booking.setDepositPaid(false);
                return setPreviousBookingForTouchUp(booking, bookableDate, customerToBook, model);
            }else{
                booking.setDepositPaid(true);
                booking.setTouchUp(false);
            }
            bookingService.saveBooking(booking);
            model.addAttribute("bookingAdded", customerToBook.getName()
                    + " booked at " + startTime + " - " + endTime
                    + " on " + date);
            model.addAttribute("bookableDate", bookableDate);

            if(bookableDate != null){
                bookableHourService.iterateThroughBookableHoursAndSetToBookedIfTheyAreBetweenStartAndEndTimeOfBooking(
                        bookableDate, startTime,endTime);
                bookableDateService.setBookableDateToFullyBookedIfAllHoursAreBooked(bookableDate);
            }
        }else{
            model.addAttribute("landingPageSingleLineMessage", "Something went wrong with non existing customer");
        }

        return "admin/admin-landing-page";
    }

    @GetMapping("/set-previous-booking-for-touch-up")
    public String setPreviousBookingForTouchUp(Booking booking, BookableDate bookableDate, Customer customer, Model model){
        List<UUID> touchedUpBookings = new ArrayList<>();
        for(Booking customerBooking: customer.getBookings()){
            if(customerBooking.getPreviousBooking() != null){
                touchedUpBookings.add(customerBooking.getPreviousBooking().getId());
            }
        }

        List<Booking> previousBookings = customer.getBookings().stream()
                .filter(b -> !b.isTouchUp() && b.getDate().isBefore(booking.getDate()) && !touchedUpBookings.contains(b.getId()))
                .toList();

        if(previousBookings.isEmpty()){
            model.addAttribute("landingPageSingleLineMessage", "Customer does not have any previous bookings");
            return "admin/admin-landing-page";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("bookableDate", bookableDate);
        model.addAttribute("customer", customer);
        model.addAttribute("previousBookings", previousBookings);

        return "admin/set-previous-date-for-touch-up";
    }

    @PostMapping("/confirm-touch-up-booking")
    public String confirmTouchUpBooking(@RequestParam UUID previousBookingId, @RequestParam LocalDateTime startDateAndTime,
                                        @RequestParam LocalDateTime endDateAndTime, @RequestParam UUID customerId,
                                        Model model){
        Customer customer = customerService.findCustomerById(customerId);
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(startDateAndTime.toLocalDate());

        if(customer != null){
            Booking booking = Booking.builder()
                    .date(startDateAndTime)
                    .endTime(endDateAndTime)
                    .customer(customer)
                    .previousBooking(bookingService.getBookingById(previousBookingId))
                    .touchUp(true)
                    .depositPaid(false)
                    .build();

            bookingService.saveBooking(booking);

            model.addAttribute("bookingAdded", customer.getName()
                    + " booked for touch up at " + startDateAndTime.toLocalTime() + " - " + endDateAndTime.toLocalTime()
                    + " on " + startDateAndTime.toLocalDate());
            model.addAttribute("bookableDate", bookableDate);

            if(bookableDate != null){
                bookableHourService.iterateThroughBookableHoursAndSetToBookedIfTheyAreBetweenStartAndEndTimeOfBooking(
                        bookableDate, startDateAndTime.toLocalTime(), endDateAndTime.toLocalTime());
                bookableDateService.setBookableDateToFullyBookedIfAllHoursAreBooked(bookableDate);
            }
        }else{
            model.addAttribute("landingPageSingleLineMessage", "Something went wrong with non existing customer");
        }

        return "admin/admin-landing-page";
    }

}
