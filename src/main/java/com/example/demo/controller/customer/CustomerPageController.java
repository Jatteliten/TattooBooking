package com.example.demo.controller.customer;

import com.example.demo.model.CustomerPageText;
import com.example.demo.services.CustomerPageTextService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerPageController {

    private final CustomerPageTextService customerPageTextService;

    public CustomerPageController(CustomerPageTextService customerPageTextService){
        this.customerPageTextService = customerPageTextService;
    }

    @GetMapping("/")
    public String frontPage(Model model){
        CustomerPageText currentText = customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                "index", "latest-news");
        if(currentText == null){
            model.addAttribute("frontPageNews", "No news to report");
        }else{
            model.addAttribute("frontPageNews", currentText.getText());
        }
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

    @GetMapping("/booking-form")
    public String bookingForm(){
        return "customer/booking-form";
    }

}
