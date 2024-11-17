package com.example.demo.controller.customer;

import com.example.demo.model.BookableDate;
import com.example.demo.services.BookableDateService;
import com.example.demo.services.TattooImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Controller
public class CustomerPageController {

    private final TattooImageService tattooImageService;
    private final BookableDateService bookableDateService;

    public CustomerPageController(TattooImageService tattooImageService, BookableDateService bookableDateService){
        this.tattooImageService = tattooImageService;
        this.bookableDateService = bookableDateService;
    }

    @RequestMapping("/")
    public String frontPage(Model model){
        model.addAttribute("images", tattooImageService.getAllFrontPageImagesUrls());
        return "index";
    }

    @RequestMapping("/about-me")
    public String aboutMe(){
        return "about-me";
    }

    @RequestMapping("/booking-calendar")
    public String getAllBookableDates(Model model){
        List<BookableDate> bookableDateList = bookableDateService.getAllCurrentlyAvailableBookableDates();
        Collections.sort(bookableDateList, Comparator.comparing(BookableDate::getDate));
        model.addAttribute("bookableDates", bookableDateList);
        return "bookable-calendar";
    }
}
