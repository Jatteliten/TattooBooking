package com.example.tattooplatform.controller.admin;

import com.example.tattooplatform.controller.ModelFeedback;
import com.example.tattooplatform.model.CustomerPageText;
import com.example.tattooplatform.model.InstagramEmbed;
import com.example.tattooplatform.services.CustomerPageTextService;
import com.example.tattooplatform.services.InstagramEmbedService;
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
    private static final String FAQ = "frequently-asked-questions";
    private static final String INDEX = "index";
    private static final String LATEST_NEWS = "latest-news";
    private static final String FRONT_PAGE_NEWS = "frontPageNews";
    private static final String CUSTOMIZE_LATEST_NEWS_TEMPLATE = "admin/customize-latest-news";

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
                INDEX, LATEST_NEWS);
        if(currentText == null){
            model.addAttribute(FRONT_PAGE_NEWS, "No news to report");
        }else{
            model.addAttribute(FRONT_PAGE_NEWS, currentText.getText());
        }
        return CUSTOMIZE_LATEST_NEWS_TEMPLATE;
    }

    @PostMapping("/update-latest-news")
    public String updateLatestNews(@RequestParam String updatedLatestNews, Model model) {
        if (updatedLatestNews == null || updatedLatestNews.trim().isEmpty()) {
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "The news text cannot be empty!");
            model.addAttribute(FRONT_PAGE_NEWS,
                    customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                            INDEX, LATEST_NEWS).getText());
        }else {
            customerPageTextService.saveCustomerPageText(CustomerPageText.builder()
                    .page(INDEX)
                    .section(LATEST_NEWS)
                    .text(updatedLatestNews)
                    .build());

            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Latest news changed!");
            model.addAttribute(FRONT_PAGE_NEWS, updatedLatestNews);
        }
        return CUSTOMIZE_LATEST_NEWS_TEMPLATE;
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

        if(!updatedPriorities.isEmpty()){
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
                        FAQ));

        return "admin/customize-frequently-asked-questions";
    }

}
