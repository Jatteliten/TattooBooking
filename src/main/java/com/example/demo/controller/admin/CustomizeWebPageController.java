package com.example.demo.controller.admin;

import com.example.demo.model.CustomerPageText;
import com.example.demo.services.CustomerPageTextService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/customize")
@PreAuthorize("hasAuthority('Admin')")
public class CustomizeWebPageController {
    private final CustomerPageTextService customerPageTextService;

    public CustomizeWebPageController(CustomerPageTextService customerPageTextService){
        this.customerPageTextService = customerPageTextService;
    }

    @GetMapping("/")
    public String customizeLandingPage(){
        return "admin/customize";
    }

    @GetMapping("/latest-news")
    public String customizeLatestNews(Model model){
        CustomerPageText currentText = customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                "index", "latest-news");
        if(currentText == null){
            model.addAttribute("frontPageNews", "No news to report");
        }else{
            model.addAttribute("frontPageNews", currentText.getText());
        }
        return "admin/customize-latest-news";
    }

    @PostMapping("/update-latest-news")
    public String updateLatestNews(Model model, @RequestParam String updatedLatestNews) {
        System.out.println(updatedLatestNews);
        if (updatedLatestNews == null || updatedLatestNews.trim().isEmpty()) {
            model.addAttribute("updated", "The news text cannot be empty!");
            model.addAttribute("frontPageNews",
                    customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                            "index", "latest-news"));
            return "admin/customize-latest-news";
        }

        customerPageTextService.saveCustomerPageText(CustomerPageText.builder()
                .page("index")
                .section("latest-news")
                .text(updatedLatestNews)
                .build());

        model.addAttribute("updated", "Latest news changed!");
        model.addAttribute("frontPageNews", updatedLatestNews);
        return "admin/customize-latest-news";
    }

}
