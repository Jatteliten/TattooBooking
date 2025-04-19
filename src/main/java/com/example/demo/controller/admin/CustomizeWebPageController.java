package com.example.demo.controller.admin;

import com.example.demo.model.CustomerPageText;
import com.example.demo.services.CustomerPageTextService;
import com.example.demo.services.InstagramEmbedService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/customize")
@PreAuthorize("hasAuthority('Admin')")
public class CustomizeWebPageController {
    private final CustomerPageTextService customerPageTextService;
    private final InstagramEmbedService instagramEmbedService;

    public CustomizeWebPageController(CustomerPageTextService customerPageTextService, InstagramEmbedService instagramEmbedService){
        this.customerPageTextService = customerPageTextService;
        this.instagramEmbedService = instagramEmbedService;
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
    public String updateLatestNews(@RequestParam String updatedLatestNews, Model model) {
        if (updatedLatestNews == null || updatedLatestNews.trim().isEmpty()) {
            model.addAttribute("updated", "The news text cannot be empty!");
            model.addAttribute("frontPageNews",
                    customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                            "index", "latest-news").getText());
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

    @GetMapping("/instagram-post")
    public String customizeInstagramLink(Model model){
        String url = instagramEmbedService.getLatestEmbed().getEmbeddedLink();
        String instagramEmbedUrl = instagramEmbedService.generateEmbedHtmlFromUrl(url);
        if(instagramEmbedUrl == null){
            model.addAttribute("noEmbedLink", "No instagram post exists with portfolio");
        }else{
            model.addAttribute("embedHtml", instagramEmbedUrl);
        }

        model.addAttribute("currentURL", url);

        return "admin/customize-portfolio-link";
    }

    @PostMapping("/update-instagram-link")
    public String updateInstagramLink(@RequestParam String updatedInstagramLink, Model model){
        instagramEmbedService.saveOrUpdateEmbed(updatedInstagramLink);
        model.addAttribute("currentURL", updatedInstagramLink);
        model.addAttribute("embedHtml", instagramEmbedService.generateEmbedHtmlFromUrl(updatedInstagramLink));

        return "admin/customize-portfolio-link";
    }

}
