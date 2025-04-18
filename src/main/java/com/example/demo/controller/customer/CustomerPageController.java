package com.example.demo.controller.customer;

import com.example.demo.model.CustomerPageText;
import com.example.demo.services.CustomerPageTextService;
import com.example.demo.services.FlashImageService;
import com.example.demo.services.ImageCategoryService;
import com.example.demo.services.InstagramEmbedService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CustomerPageController {

    private final CustomerPageTextService customerPageTextService;
    private final InstagramEmbedService instagramEmbedService;
    private final FlashImageService flashImageService;
    private final ImageCategoryService imageCategoryService;

    public CustomerPageController(CustomerPageTextService customerPageTextService, InstagramEmbedService instagramEmbedService, FlashImageService flashImageService, ImageCategoryService imageCategoryService){
        this.customerPageTextService = customerPageTextService;
        this.instagramEmbedService = instagramEmbedService;
        this.flashImageService = flashImageService;
        this.imageCategoryService = imageCategoryService;
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
    public String contact(){return "customer/contact";}

    @GetMapping("/about-me")
    public String aboutMe(){
        return "customer/about-me";
    }

    @GetMapping("/booking-form")
    public String bookingForm(){
        return "customer/booking-form";
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {
        model.addAttribute("embedHtml", instagramEmbedService.generateEmbedHtmlFromDatabase());

        return "customer/portfolio";
    }

    @GetMapping("/flash")
    public String flash(Model model){
        model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        model.addAttribute("flashes", flashImageService.getAllFlashImagesMapByCategory());

        return "customer/available-flash";
    }

}
