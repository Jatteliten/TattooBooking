package com.example.demo.controller.customer;

import com.example.demo.model.CustomerPageText;
import com.example.demo.model.ImageCategory;
import com.example.demo.services.CustomerPageTextService;
import com.example.demo.services.FlashImageService;
import com.example.demo.services.ImageCategoryService;
import com.example.demo.services.InstagramEmbedService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
        String instagramEmbedHtml = instagramEmbedService.generateEmbedHtmlFromUrl(
                instagramEmbedService.getLatestEmbed().getEmbeddedLink());
        if(instagramEmbedHtml == null){
            model.addAttribute("noEmbedLink", "No instagram post exists with portfolio..");
        }else{
            model.addAttribute("embedHtml", instagramEmbedHtml);
        }

        return "customer/portfolio";
    }

    @GetMapping("/flash")
    public String viewFlashCategories(Model model){
        model.addAttribute("categories",
                imageCategoryService.convertImageCategoryListToImageCategoryWithOnlyCategoryDtoList(
                        imageCategoryService.filterImageCategoriesWithoutFlashImages(
                        imageCategoryService.getAllImageCategories())));

        return "customer/flash-categories";
    }

    @GetMapping("/flash-with-category")
    public String viewFlashesWithCategories(@RequestParam String categoryName, Model model){
        ImageCategory imageCategory = imageCategoryService.findImageCategoryByCategoryName(categoryName);
        if(imageCategory.getFlashImages().isEmpty()){
            return "error";
        }else{
            model.addAttribute("category", categoryName);
            model.addAttribute("flashes", flashImageService.convertFlashImageListToFlashImagesOnlyUrlDTO(
                    flashImageService.getFlashImagesByCategory(
                            imageCategoryService.findImageCategoryByCategoryName(
                                    categoryName))));

            return "customer/available-flash-with-category";
        }
    }


}
