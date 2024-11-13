package com.example.demo.controller;

import com.example.demo.model.TattooImage;
import com.example.demo.model.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.services.BookingService;
import com.example.demo.services.CustomerService;
import com.example.demo.services.TattooImageService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('Admin')")
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
    public String bookTattoo(){
        return "book-tattoo";
    }

    @RequestMapping("/bookings")
    public String displayBookingsForAdmin(Model model){
        model.addAttribute("upcomingBookings",
                bookingService.getBookingsFromThisDateToFourWeeksForward().stream()
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

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            TattooImage image = tattooImageService.saveImage(file);
            model.addAttribute("message", "Image uploaded successfully: " + image.getName());
        } catch (IOException e) {
            model.addAttribute("message", "Failed to upload image");
        }
        model.addAttribute("images", tattooImageService.getAllImages());
        return "redirect:/admin/set-front-page-images";
    }

    @RequestMapping("/set-front-page-images")
    public String setFrontPageImage(Model model){
        model.addAttribute("imagesOnFrontPage", tattooImageService.getAllFrontPageImages());
        model.addAttribute("imagesNotOnFrontPage", tattooImageService.getAllNonFrontPageImages());
        return "select-front-page-images";
    }

    @RequestMapping("/changeImageState")
    public String changeFrontPageState(@RequestParam String url, Model model){
        tattooImageService.changeTattooImageStateToOppositeByUrl(url);

        model.addAttribute("imagesOnFrontPage", tattooImageService.getAllFrontPageImages());
        model.addAttribute("imagesNotOnFrontPage", tattooImageService.getAllNonFrontPageImages());
        return "select-front-page-images";
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> viewImage(@PathVariable Long id) throws MalformedURLException {
        TattooImage image = tattooImageService.getImage(id);
        Path imagePath = Paths.get(image.getUrl());
        Resource resource = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(resource);
    }

    @GetMapping("/uploads")
    public String viewUploadsPage(Model model) {
        model.addAttribute("images", tattooImageService.getAllImages());
        return "uploads";
    }

    @RequestMapping("/customer")
    public String customerInformation(@RequestParam String searchWord, Model model){
        model.addAttribute("customer", customerService.findCustomerByPhoneInstagramOrEmail(searchWord));
        return "customer";
    }
}
