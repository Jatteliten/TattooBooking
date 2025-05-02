package com.example.tattooPlatform.controller.admin;

import com.example.tattooPlatform.model.CustomerPageText;
import com.example.tattooPlatform.model.InstagramEmbed;
import com.example.tattooPlatform.services.CustomerPageTextService;
import com.example.tattooPlatform.services.InstagramEmbedService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/customize")
@PreAuthorize("hasAuthority('Admin')")
public class CustomizeWebPageController {
    private final CustomerPageTextService customerPageTextService;
    private final InstagramEmbedService instagramEmbedService;
    private final static String FAQ = "frequently-asked-questions";

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
        InstagramEmbed latestInstagramPost = instagramEmbedService.getLatestEmbed();
        String url = "empty";

        if(latestInstagramPost == null){
            model.addAttribute("noEmbedLink", "No instagram post exists with portfolio");
        }else{
            url = latestInstagramPost.getEmbeddedLink();
            String instagramEmbedUrl = instagramEmbedService.generateEmbedHtmlFromUrl(url);
            model.addAttribute("embedHtml", instagramEmbedUrl);
        }


        model.addAttribute("currentURL", url);

        return "admin/customize-portfolio-link";
    }

    @PostMapping("/update-instagram-link")
    public String updateInstagramLink(@RequestParam String updatedInstagramLink, Model model){
        instagramEmbedService.saveInstagramEmbed(InstagramEmbed.builder()
                .embeddedLink(updatedInstagramLink)
                .createdAt(LocalDateTime.now())
                .build());
        model.addAttribute("currentURL", updatedInstagramLink);
        model.addAttribute("embedHtml", instagramEmbedService.generateEmbedHtmlFromUrl(updatedInstagramLink));

        return "admin/customize-portfolio-link";
    }

    @GetMapping("/frequently-asked-questions")
    public String customizeFrequentlyAskedQuestions(Model model){
        return populateFaqModelAndReturnPage(model);
    }
    
    @PostMapping("/add-frequently-asked-question")
    public String saveFrequentlyAskedQuestion(@RequestParam String question, @RequestParam String answer, Model model){
        customerPageTextService.saveCustomerPageText(CustomerPageText.builder()
                .page(FAQ)
                .section(question)
                .text(answer)
                .priority(customerPageTextService.countCustomerPageTextsByPage(FAQ) + 1)
                .build());

        return populateFaqModelAndReturnPage(model);
    }

    @PostMapping("/change-frequently-asked-question-priority")
    public String changeFrequentlyAskedQuestionPriority(@RequestParam UUID id, boolean decrement, Model model){
        CustomerPageText priorityToUpdate = customerPageTextService.getCustomerPageTextById(id);

        if(priorityToUpdate == null){
            return populateFaqModelAndReturnPage(model);
        }

        int change = decrement ? -1 : 1;
        List<CustomerPageText> updatedPriorities = customerPageTextService.switchPriorities(decrement, priorityToUpdate,
                customerPageTextService.getCustomerPageTextByPageAndPriority(
                        FAQ, priorityToUpdate.getPriority() + change));

        if(updatedPriorities != null){
            customerPageTextService.saveListOfCustomerPageTexts(updatedPriorities);
        }

        return populateFaqModelAndReturnPage(model);
    }

    @PostMapping("/delete-frequently-asked-question")
    public String deleteFrequentlyAskedQuestion(@RequestParam UUID id, Model model){
        customerPageTextService.reassignPrioritiesOnDelete(
                customerPageTextService.getCustomerPageTextListByPageSortedByAscendingPriority(FAQ),
                customerPageTextService.getCustomerPageTextById(id));

        return populateFaqModelAndReturnPage(model);
    }

    private String populateFaqModelAndReturnPage(Model model) {
        model.addAttribute("questions" ,
                customerPageTextService.getCustomerPageTextListByPageSortedByAscendingPriority(
                        "frequently-asked-questions"));

        return "admin/customize-frequently-asked-questions";
    }

}
