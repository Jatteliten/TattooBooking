package com.example.demo.controller.customer;

import com.example.demo.services.BookableDateService;
import com.example.demo.services.TattooImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
