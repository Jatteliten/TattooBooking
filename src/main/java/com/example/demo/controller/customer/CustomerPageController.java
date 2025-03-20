package com.example.demo.controller.customer;

import com.example.demo.services.BookableDateService;
import com.example.demo.services.TattooImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerPageController {

    private final TattooImageService tattooImageService;
    private final BookableDateService bookableDateService;

    public CustomerPageController(TattooImageService tattooImageService, BookableDateService bookableDateService){
        this.tattooImageService = tattooImageService;
        this.bookableDateService = bookableDateService;
    }

    @GetMapping("/")
    public String frontPage(Model model){
        model.addAttribute("images", tattooImageService.getAllFrontPageImagesUrls());
        return "index";
    }

    @GetMapping("/about-me")
    public String aboutMe(){
        return "customer/about-me";
    }

    @GetMapping("/booking-calendar")
    public String getAllBookableDates(Model model){
        model.addAttribute("bookableDates",
                bookableDateService.sortHoursInBookableHourListByHour(
                        bookableDateService.sortBookableDateListByDate(
                                bookableDateService.getAllCurrentlyAvailableBookableDates())));
        return "customer/bookable-calendar";
    }
}
