package com.example.tattooPlatform.controller.customer;

import com.example.tattooPlatform.dto.customerpagetext.CustomerPageTextDto;
import com.example.tattooPlatform.model.ImageCategory;
import com.example.tattooPlatform.model.InstagramEmbed;
import com.example.tattooPlatform.model.ProductCategory;
import com.example.tattooPlatform.services.CustomerPageTextService;
import com.example.tattooPlatform.services.FlashImageService;
import com.example.tattooPlatform.services.ImageCategoryService;
import com.example.tattooPlatform.services.InstagramEmbedService;
import com.example.tattooPlatform.services.ProductCategoryService;
import com.example.tattooPlatform.services.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


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
        CustomerPageTextDto currentText = customerPageTextService.convertCustomerPageTextToCustomerPageTextDto(
                customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                "index", "latest-news"));
        if(currentText == null){
            model.addAttribute("frontPageNews", "No news to report");
        }else{
            model.addAttribute("frontPageNews", currentText.getText());
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
            String instagramEmbedHtml = instagramEmbedService.generateEmbedHtmlFromUrl(latestInstagramPost.getEmbeddedLink());
            model.addAttribute("embedHtml", instagramEmbedHtml);
        }

        return "customer/portfolio";
    }

    @GetMapping("/flash")
    public String viewFlashCategories(Model model){
        model.addAttribute("categories",
                imageCategoryService.convertImageCategoryListToImageCategoryDtoList(
                        imageCategoryService.filterImageCategoriesWithoutFlashImages(
                        imageCategoryService.getAllImageCategories())));

        return "customer/flash-categories";
    }

    @GetMapping("/flash/{categoryName}")
    public String viewFlashesWithCategories(@PathVariable String categoryName, Model model){
        ImageCategory imageCategory = imageCategoryService.getImageCategoryByCategoryName(categoryName);
        if(imageCategory.getFlashImages().isEmpty()){
            return "error";
        }else{
            model.addAttribute("category", categoryName);
            model.addAttribute("flashes", flashImageService.convertFlashImageListToFlashImagesUrlDto(
                    flashImageService.getFlashImagesByCategory(imageCategory)));

            return "customer/available-flash-with-category";
        }
    }

    @GetMapping("/frequently-asked-questions")
    public String viewFrequentlyAskedQuestions(Model model){
        model.addAttribute("questions",
                customerPageTextService.convertCustomerPageTextListToCustomerPageTextPrioritizedDtoList(
                customerPageTextService.getCustomerPageTextListByPageSortedByPriority("frequently-asked-questions")));

        return "customer/frequently-asked-questions";
    }

    @GetMapping("/products")
    public String viewProductCategories(Model model){
        model.addAttribute("categories",
                productCategoryService.convertProductCategoryListToProductCategoryDtoList(
                productCategoryService.filterOutProductCategoriesWithoutProducts(
                        productCategoryService.getAllProductCategories())));

        return "customer/product-categories";
    }

    @GetMapping("/products/{categoryName}")
    public String viewProductsByCategory(@PathVariable String categoryName, Model model){
        ProductCategory productCategory = productCategoryService.getProductCategoryByName(categoryName);

        model.addAttribute("products",
                productService.convertProductListToProductCustomerViewDtoList(
                        productCategory.getProducts()));
        model.addAttribute("category", productCategory.getName());
        return "customer/products-with-category";
    }

    @GetMapping("/mail-confirmation")
    public String mailConfirmation(Model model, @ModelAttribute("mailSent") Boolean mailSent){
        model.addAttribute("mailSent", mailSent);
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
