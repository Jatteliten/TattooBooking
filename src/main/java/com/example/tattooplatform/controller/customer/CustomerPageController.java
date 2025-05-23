package com.example.tattooplatform.controller.customer;

import com.example.tattooplatform.model.CustomerPageText;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.model.InstagramEmbed;
import com.example.tattooplatform.model.ProductCategory;
import com.example.tattooplatform.services.CustomerPageTextService;
import com.example.tattooplatform.services.FlashImageService;
import com.example.tattooplatform.services.ImageCategoryService;
import com.example.tattooplatform.services.InstagramEmbedService;
import com.example.tattooplatform.services.ProductCategoryService;
import com.example.tattooplatform.services.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


@Controller
public class CustomerPageController {
    @Value("${artist.mail}")
    private String artistMail;
    @Value("${artist.instagram}")
    private String artistInstagram;
    @Value("${artist.tiktok}")
    private String artistTiktok;
    @Value("${artist.facebook.link}")
    private String artistFacebookLink;

    private final CustomerPageTextService customerPageTextService;
    private final InstagramEmbedService instagramEmbedService;
    private final FlashImageService flashImageService;
    private final ImageCategoryService imageCategoryService;
    private final ProductCategoryService productCategoryService;
    private final ProductService productService;

    public CustomerPageController(CustomerPageTextService customerPageTextService,
                                  InstagramEmbedService instagramEmbedService,
                                  FlashImageService flashImageService,
                                  ImageCategoryService imageCategoryService,
                                  ProductCategoryService productCategoryService,
                                  ProductService productService){
        this.customerPageTextService = customerPageTextService;
        this.instagramEmbedService = instagramEmbedService;
        this.flashImageService = flashImageService;
        this.imageCategoryService = imageCategoryService;
        this.productCategoryService = productCategoryService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String frontPage(Model model){
        CustomerPageText currentText = customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                "index", "latest-news");
        if(currentText == null){
            model.addAttribute("frontPageNews", "No news to report");
        }else{
            model.addAttribute("frontPageNews",
                    customerPageTextService.convertCustomerPageTextToCustomerPageTextDto(currentText).getText());
        }
        return "index";
    }

    @GetMapping("/care-advice")
    public String careAdvice(){ return "customer/care-advice";}

    @GetMapping("/before-your-visit")
    public String beforeYourVisit() { return "customer/before-your-visit"; }

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
        InstagramEmbed latestInstagramPost = instagramEmbedService.getLatestEmbed();
        if(latestInstagramPost == null){
            model.addAttribute("noEmbedLink", "No instagram post exists with portfolio..");
        }else{
            model.addAttribute("embedHtml",
                    instagramEmbedService.generateEmbedHtmlFromUrl(latestInstagramPost.getEmbeddedLink()));
        }

        return "customer/portfolio";
    }

    @GetMapping("/flash")
    public String viewFlashCategories(Model model){
        populateFlashCategories(model);

        return "customer/flash-categories";
    }

    @GetMapping("/flash/{categoryName}")
    public String viewFlashesWithCategories(@PathVariable String categoryName,
                                            @RequestParam(defaultValue = "0") int page,
                                            Model model){
        ImageCategory imageCategory = imageCategoryService.getImageCategoryByCategoryName(categoryName);

        if (imageCategory == null || imageCategory.getFlashImages().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        populateFlashCategories(model);
        model.addAttribute("category", categoryName);
        model.addAttribute("flashes",
                flashImageService.getFlashImageUrlDTOssByCategoryPaginated(imageCategory, PageRequest.of(page, 12)));
        return "customer/available-flash-with-category";
    }


    private void populateFlashCategories(Model model) {
        model.addAttribute("categories",
                imageCategoryService.convertImageCategoryListToImageCategoryDtoList(
                        imageCategoryService.filterImageCategoriesWithoutFlashImages(
                                imageCategoryService.sortImageCategoriesByName(
                                        imageCategoryService.getAllImageCategories()))));
    }

    @GetMapping("/frequently-asked-questions")
    public String viewFrequentlyAskedQuestions(Model model){
        model.addAttribute("questions",
                customerPageTextService.convertCustomerPageTextListToCustomerPageTextPrioritizedDtoList(
                customerPageTextService.getCustomerPageTextListByPageSortedByAscendingPriority("frequently-asked-questions")));

        return "customer/frequently-asked-questions";
    }

    @GetMapping("/products")
    public String viewProductCategories(Model model){
        populateProductCategories(model);

        return "customer/product-categories";
    }

    @GetMapping("/products/{categoryName}")
    public String viewProductsByCategory(@PathVariable String categoryName, Model model){
        ProductCategory productCategory = productCategoryService.getProductCategoryByName(categoryName);

        if (productCategory == null || productCategory.getProducts().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        populateProductCategories(model);
        model.addAttribute("products",
                productService.convertProductListToProductCustomerViewDtoList(
                        productCategory.getProducts()));
        model.addAttribute("category", productCategory.getName());
        return "customer/products-with-category";
    }

    private void populateProductCategories(Model model) {
        model.addAttribute("categories",
                productCategoryService.convertProductCategoryListToProductCategoryDtoList(
                        productCategoryService.filterOutProductCategoriesWithoutProducts(
                                productCategoryService.getAllProductCategories())));
    }

    @GetMapping("/privacy-notice")
    public String privacyNotice() {
        return "customer/privacy-notice";
    }

    @GetMapping("/mail-confirmation")
    public String mailConfirmation(){
        return "customer/mail-confirmation";
    }

    @ModelAttribute("artistMail")
    public String getArtistMail() {
        return artistMail;
    }

    @ModelAttribute("artistInstagram")
    public String getArtistInstagram() {
        return artistInstagram;
    }

    @ModelAttribute("artistTiktok")
    public String getArtistTiktok() {
        return artistTiktok;
    }

    @ModelAttribute("artistFacebookLink")
    public String getArtistFacebookLink() {
        return artistFacebookLink;
    }

}
