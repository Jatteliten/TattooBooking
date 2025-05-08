package com.example.tattooplatform.controller.admin;

import com.example.tattooplatform.controller.ModelFeedback;
import com.example.tattooplatform.model.BookableDate;
import com.example.tattooplatform.model.Booking;
import com.example.tattooplatform.model.Customer;
import com.example.tattooplatform.services.BookableDateService;
import com.example.tattooplatform.services.BookableHourService;
import com.example.tattooplatform.services.BookingService;
import com.example.tattooplatform.services.CalendarService;
import com.example.tattooplatform.services.CustomerService;
import com.example.tattooplatform.services.ImageCategoryService;
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
    private static final String BOOKING = "booking";
    private static final String SEARCH_RESULT = "searchResult";
    private static final String CATEGORIES = "categories";
    private static final String BOOKABLE_DATE = "bookableDate";
    private static final String ADMIN_LANDING_PAGE_TEMPLATE = "admin/admin-landing-page";
    private static final String BOOKING_INFORMATION_TEMPLATE = "admin/booking-information";

    public BookingController(CalendarService calendarService, BookingService bookingService,
                             CustomerService customerService, BookableDateService bookableDateService,
                             BookableHourService bookableHourService, ImageCategoryService imageCategoryService){
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
            bookings = bookingService.sortBookingsByStartDateAndTime(
                    bookingService.getBookingsBetweenTwoGivenDates(
                    LocalDateTime.of(fromDate, LocalTime.of(0,0)),
                    LocalDateTime.of(toDate, LocalTime.of(23,59))));
        }else{
            bookings = bookingService.sortBookingsByStartDateAndTime(
                    bookingService.sortBookingsByStartDateAndTime(bookingService.getBookingsByDate(LocalDate.now())));
        }

        if(!bookings.isEmpty()){
            model.addAttribute("totalCost", bookingService.calculateTotalCostForBookings(bookings));
            model.addAttribute("upcomingBookings", bookings);
        }else if(fromDate != null && toDate != null){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "No bookings on given dates");
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "No bookings today");
        }
        return "admin/bookings";
    }

    @GetMapping("/booking-information")
    public String viewBookingInformation(@RequestParam UUID id, Model model){
        model.addAttribute(BOOKING, bookingService.getBookingById(id));
        model.addAttribute(CATEGORIES,
                imageCategoryService.sortImageCategoriesByName(imageCategoryService.getAllImageCategories()));
        return BOOKING_INFORMATION_TEMPLATE;
    }

    @PostMapping("/update-booking-notes")
    public String updateBookingNotes(@RequestParam String notes, @RequestParam UUID id, Model model){
        Booking booking = bookingService.getBookingById(id);
        booking.setNotes(notes);
        bookingService.saveBooking(booking);

        return populateModelOnBookingUpdate(model, booking, "Booking notes saved.");
    }

    @PostMapping("/change-booking-deposit-state")
    public String changeBookingDepositState(@RequestParam UUID id, Model model){
        Booking booking = bookingService.getBookingById(id);
        booking.setDepositPaid(!booking.isDepositPaid());
        bookingService.saveBooking(booking);
        String newValue = booking.isDepositPaid() ? "Yes" : "No";

        return populateModelOnBookingUpdate(model, booking, "Booking deposit changed to \"" + newValue + "\".");
    }

    @PostMapping("/cancel-appointment")
    public String removeBooking(@RequestParam UUID id, Model model){
        Booking booking = bookingService.getBookingById(id);
        bookingService.deleteBooking(booking);
        model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Cancelled " + booking.getCustomer().getName() +
                " at " + booking.getDate().toLocalDate() + " | " + booking.getDate().toLocalTime());
        return "admin/bookings";
    }

    @PostMapping("/upload-image")
    public String uploadBookingImage(@RequestParam("file") MultipartFile file,
                                     @RequestParam UUID id,
                                     @RequestParam(value="categoryIds", required=false) List<UUID> categoryIds,
                                     Model model){
        if(categoryIds == null){
            return populateModelOnFailedBookingUpdate(model, bookingService.getBookingById(id),
                    "You must pick at least one category");
        }else{
            try {
                return populateModelOnBookingUpdate(model,
                        bookingService.uploadTattooImage(bookingService.getBookingById(id), file, categoryIds),
                        "Tattoo image uploaded!");
            } catch (Exception e) {
                return populateModelOnFailedBookingUpdate(model, bookingService.getBookingById(id),
                        "Failed to upload image.");
            }
        }
    }

    @PostMapping("/delete-image")
    public String deleteTattooImage(@RequestParam UUID id, Model model){
        try {
            Booking booking = bookingService.getBookingById(id);
            bookingService.deleteTattooImageFromBooking(booking);
            bookingService.saveBooking(booking);
            return populateModelOnBookingUpdate(model, booking, "Tattoo image deleted!");
        } catch (Exception e) {
            return populateModelOnFailedBookingUpdate(model, bookingService.getBookingById(id),
                    "Failed to delete image.");
        }
    }

    @GetMapping("/book-tattoo-at-date")
    public String bookTattooWithGivenDate(@RequestParam LocalDate date, Model model){
        return addBookableHoursToModelIfBookableDateExistsByDate(date, model);
    }

    @GetMapping("/search-customer")
    public String searchCustomer(@RequestParam String searchInput, @RequestParam LocalDate date,
                                 Model model){
        Customer customer = customerService.getCustomerByAnyField(searchInput);
        model.addAttribute(SEARCH_RESULT,
                Objects.requireNonNullElse(customer, "No customer found"));

        return addBookableHoursToModelIfBookableDateExistsByDate(date, model);
    }

    @PostMapping("/create-customer")
    public String createNewCustomer(@RequestParam LocalDate date,
                                    @RequestParam String name,
                                    @RequestParam(required = false) String phone,
                                    @RequestParam(required = false) String instagram,
                                    @RequestParam(required = false) String email, Model model) {
        phone = trimAndNull(phone);
        instagram = trimAndNull(instagram);
        email = trimAndNull(email);

        Customer customer = Customer.builder()
                .name(name)
                .phone(phone)
                .instagram(instagram)
                .email(email)
                .build();

        Customer existingCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(customer);
        if (existingCustomer == null) {
            customerService.saveCustomer(customer);
            model.addAttribute(SEARCH_RESULT, customer);
        } else {
            model.addAttribute(SEARCH_RESULT, existingCustomer);
        }

        model.addAttribute("selectedDate", date);
        return addBookableHoursToModelIfBookableDateExistsByDate(date, model);
    }

    private String trimAndNull(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    @PostMapping("/book-tattoo-with-customer")
    public String bookSessionWithCustomer(@RequestParam LocalDate date,
                                          @RequestParam LocalTime startTime,
                                          @RequestParam LocalTime endTime,
                                          @RequestParam String customerEmail,
                                          @RequestParam String customerInstagram,
                                          @RequestParam String customerPhone,
                                          @RequestParam(required = false) Boolean touchUp,
                                          @RequestParam(required = false) Boolean depositPaid, Model model){
        LocalDateTime startDateAndTime = date.atTime(startTime);
        LocalDateTime endDateAndTime = date.atTime(endTime);

        if(startTime.equals(LocalTime.of(0,0)) || endTime.equals(LocalTime.of(23, 59))){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "Can't set start time to 00:00 or end time to 23:59");
            return ADMIN_LANDING_PAGE_TEMPLATE;
        }

        if(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(startDateAndTime, endDateAndTime)){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "Can't book at already booked hours");
            return ADMIN_LANDING_PAGE_TEMPLATE;
        }

        Customer customerToBook = customerService.getCustomerIfAtLeastOneContactMethodMatches(
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
                    .depositPaid(depositPaid != null)
                    .build();

            if(touchUp != null && touchUp){
                booking.setTouchUp(true);
                booking.setDepositPaid(false);
                return setPreviousBookingForTouchUp(booking, bookableDate, customerToBook, model);
            }else{
                booking.setTouchUp(false);
            }

            bookingService.saveBooking(booking);
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), bookingService.createBookingSuccessMessage(
                    customerToBook, startTime, endTime, date));
            model.addAttribute(BOOKABLE_DATE, bookableDate);

            if(bookableDate != null && !bookableDate.isDropIn()){
                bookableHourService.setBookableHoursInBookableDateToBookedBetweenStartAndEndTime(
                        bookableDate, startTime, endTime);
                bookableHourService.saveAllBookableHours(bookableDate.getBookableHours());
                bookableDateService.setBookableDateToFullyBookedIfAllHoursAreBooked(bookableDate);
            }
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "Something went wrong with non existing customer");
        }

        return ADMIN_LANDING_PAGE_TEMPLATE;
    }

    @GetMapping("/set-previous-booking-for-touch-up")
    public String setPreviousBookingForTouchUp(
            Booking booking, BookableDate bookableDate, Customer customer, Model model){
        List<Booking> previousBookings = bookingService.sortBookingsByStartDateAndTime(
                customerService.getCustomersEligiblePreviousBookingsForTouchUp(
                customer, booking));

        if(previousBookings.isEmpty()){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "Customer does not have any previous bookings");
            return ADMIN_LANDING_PAGE_TEMPLATE;
        }

        model.addAttribute(BOOKING, booking);
        model.addAttribute(BOOKABLE_DATE, bookableDate);
        model.addAttribute("customer", customer);
        model.addAttribute("previousBookings", previousBookings);

        return "admin/set-previous-date-for-touch-up";
    }

    @PostMapping("/confirm-touch-up-booking")
    public String confirmTouchUpBooking(@RequestParam UUID previousBookingId,
                                        @RequestParam LocalDateTime startDateAndTime,
                                        @RequestParam LocalDateTime endDateAndTime,
                                        @RequestParam UUID customerId,
                                        Model model){
        Customer customer = customerService.getCustomerById(customerId);
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

            model.addAttribute("bookingAdded", bookingService.createBookingSuccessMessage(customer,
                    startDateAndTime.toLocalTime(), endDateAndTime.toLocalTime(), startDateAndTime.toLocalDate()));
            model.addAttribute(BOOKABLE_DATE, bookableDate);

            if(bookableDate != null){
                bookableHourService.setBookableHoursInBookableDateToBookedBetweenStartAndEndTime(
                        bookableDate, startDateAndTime.toLocalTime(), endDateAndTime.toLocalTime());
                bookableHourService.saveAllBookableHours(bookableDate.getBookableHours());
                bookableDateService.setBookableDateToFullyBookedIfAllHoursAreBooked(bookableDate);
            }
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "Something went wrong with non existing customer");
        }

        return ADMIN_LANDING_PAGE_TEMPLATE;
    }

    @PostMapping("/update-booking-price")
    public String updateBookingPrice(@RequestParam int price, @RequestParam UUID id, Model model){
        Booking booking = bookingService.getBookingById(id);
        booking.setFinalPrice(price);
        bookingService.saveBooking(booking);

        return populateModelOnBookingUpdate(model, booking, "Price updated.");
    }

    private String populateModelOnFailedBookingUpdate(Model model, Booking booking, String errorMessage){
        model.addAttribute(BOOKING, booking);
        model.addAttribute(CATEGORIES,
                imageCategoryService.sortImageCategoriesByName(imageCategoryService.getAllImageCategories()));
        model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), errorMessage);
        return BOOKING_INFORMATION_TEMPLATE;
    }
    private String populateModelOnBookingUpdate(Model model, Booking booking, String updateFeedback) {
        model.addAttribute(BOOKING, booking);
        model.addAttribute(CATEGORIES,
                imageCategoryService.sortImageCategoriesByName(imageCategoryService.getAllImageCategories()));
        model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), updateFeedback);
        return BOOKING_INFORMATION_TEMPLATE;
    }

    private String addBookableHoursToModelIfBookableDateExistsByDate(LocalDate date, Model model) {
        model.addAttribute("bookingsAtDate", bookingService.sortBookingsByStartDateAndTime(
                bookingService.sortBookingsByStartDateAndTime(bookingService.getBookingsByDate(date))));
        model.addAttribute("selectedDate", date);
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(date);
        if(bookableDate != null) {
            model.addAttribute("bookableHours", bookableDate.getBookableHours());
        }
        return "admin/book-tattoo-with-date";
    }

}