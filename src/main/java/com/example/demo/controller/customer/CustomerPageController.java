package com.example.demo.controller.customer;

import com.example.demo.model.CustomerPageText;
import com.example.demo.model.ImageCategory;
import com.example.demo.model.ProductCategory;
import com.example.demo.services.CustomerPageTextService;
import com.example.demo.services.FlashImageService;
import com.example.demo.services.ImageCategoryService;
import com.example.demo.services.InstagramEmbedService;
import com.example.demo.services.ProductCategoryService;
import com.example.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


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

    public CustomerPageController(CustomerPageTextService customerPageTextService, InstagramEmbedService instagramEmbedService, FlashImageService flashImageService, ImageCategoryService imageCategoryService, ProductCategoryService productCategoryService, ProductService productService){
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

    @GetMapping("/frequently-asked-questions")
    public String viewFrequentlyAskedQuestions(Model model){
        model.addAttribute("questions",
                customerPageTextService.getCustomerPageTextListByPage("frequently-asked-questions"));

        return "customer/frequently-asked-questions";
    }

    @GetMapping("/products")
    public String viewProductCategories(Model model){
        model.addAttribute("categories", productCategoryService.convertProductCategoryListToProductCategoryOnlyNameDTOList(
                productCategoryService.filterOutProductCategoriesWithoutProducts(
                        productCategoryService.getAllProductCategories())));

        return "customer/product-categories";
    }

    @GetMapping("/products-with-category")
    public String viewProductsByCategory(@RequestParam String categoryName, Model model){
        ProductCategory productCategory = productCategoryService.getProductCategoryByName(categoryName);

        model.addAttribute("products",
                productService.convertProductListToProductWithNameDescriptionPriceImageUrlDtoList(
                        productCategory.getProducts()));
        model.addAttribute("category", productCategory.getName());
        return "customer/products-with-category";
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
