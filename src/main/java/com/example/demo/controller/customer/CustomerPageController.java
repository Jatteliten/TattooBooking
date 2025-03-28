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
        model.addAttribute("frontPageText", "hej Jag heter Tana och jag är en liten apa. Det här är min hemsida hejhej");
        return "index";
    }

    @GetMapping("/contact")
    public String contact(){
        return "customer/contact";
    }

    @GetMapping("/about-me")
    public String aboutMe(){
        return "customer/about-me";
    }

}
