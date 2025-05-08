package com.example.tattooplatform.controller.customer;

import com.example.tattooplatform.dto.customerpagetext.CustomerPageTextDto;
import com.example.tattooplatform.dto.product.ProductCustomerViewDto;
import com.example.tattooplatform.model.CustomerPageText;
import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.model.InstagramEmbed;
import com.example.tattooplatform.model.Product;
import com.example.tattooplatform.model.ProductCategory;
import com.example.tattooplatform.services.CustomerPageTextService;
import com.example.tattooplatform.services.FlashImageService;
import com.example.tattooplatform.services.ImageCategoryService;
import com.example.tattooplatform.services.InstagramEmbedService;
import com.example.tattooplatform.services.ProductCategoryService;
import com.example.tattooplatform.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerPageController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerPageTextService customerPageTextService;

    @MockBean
    private InstagramEmbedService instagramEmbedService;

    @MockBean
    private FlashImageService flashImageService;

    @MockBean
    private ImageCategoryService imageCategoryService;

    @MockBean
    private ProductCategoryService productCategoryService;

    @MockBean
    private ProductService productService;

    @Test
    void frontPage_withNews_displaysNews() throws Exception {
        CustomerPageTextDto customerPageText = CustomerPageTextDto.builder()
                .text("Latest news")
                .build();

        when(customerPageTextService.getLatestCustomerPageTextByPageAndSection("index", "latest-news"))
                .thenReturn(new CustomerPageText());

        when(customerPageTextService.convertCustomerPageTextToCustomerPageTextDto(
                        customerPageTextService.getLatestCustomerPageTextByPageAndSection(
                                "index", "latest-news")))
                .thenReturn(customerPageText);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("frontPageNews", "Latest news"));
    }

    @Test
    void frontPage_withoutNews_displaysDefaultMessage() throws Exception {
        when(customerPageTextService.getLatestCustomerPageTextByPageAndSection("index", "latest-news"))
                .thenReturn(null);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("frontPageNews", "No news to report"));
    }

    @Test
    void careAdvice_returnsCareAdvicePage() throws Exception {
        mockMvc.perform(get("/care-advice"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/care-advice"));
    }

    @Test
    void beforeYourVisit_returnsBeforeYourVisitPage() throws Exception {
        mockMvc.perform(get("/before-your-visit"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/before-your-visit"));
    }

    @Test
    void aboutMe_returnsAboutMePage() throws Exception {
        mockMvc.perform(get("/about-me"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/about-me"));
    }

    @Test
    void bookingForm_returnsBookingFormPage() throws Exception {
        mockMvc.perform(get("/booking-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/booking-form"));
    }

    @Test
    void portfolio_displaysPortfolio_ifInstagramEmbedLinkIsPresent() throws Exception{
        when(instagramEmbedService.getLatestEmbed()).thenReturn(InstagramEmbed.builder().embeddedLink("test").build());
        when(instagramEmbedService.generateEmbedHtmlFromUrl(Mockito.anyString())).thenReturn("test");

        mockMvc.perform(get("/portfolio"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/portfolio"))
                .andExpect(model().attribute("embedHtml", "test"));
    }

    @Test
    void portfolio_displaysNoPortfolio_ifInstagramEmbedLinkIsNotPresent() throws Exception {
        when(instagramEmbedService.getLatestEmbed()).thenReturn(null);

        mockMvc.perform(get("/portfolio"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/portfolio"))
                .andExpect(model().attribute("noEmbedLink", "No instagram post exists with portfolio.."));
    }

    @Test
    void viewFlashCategories_returnsFlashCategories() throws Exception {
        when(imageCategoryService.getAllImageCategories()).thenReturn(List.of());
        when(imageCategoryService.filterImageCategoriesWithoutFlashImages(Mockito.anyList())).thenReturn(List.of());
        when(imageCategoryService.convertImageCategoryListToImageCategoryDtoList(Mockito.anyList())).thenReturn(List.of());

        mockMvc.perform(get("/flash"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/flash-categories"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void viewFlashesWithCategories_returnsAvailableFlashPage_whenFlashImagesExist() throws Exception {
        ImageCategory imageCategory = ImageCategory.builder()
                .category("testCategory")
                .flashImages(List.of(new FlashImage()))
                .build();

        when(imageCategoryService.getImageCategoryByCategoryName("testCategory")).thenReturn(imageCategory);
        when(flashImageService.getFlashImagesByCategory(imageCategory)).thenReturn(List.of(new FlashImage()));
        when(flashImageService.convertFlashImageListToFlashImagesUrlDto(Mockito.anyList())).thenReturn(List.of());

        mockMvc.perform(get("/flash/" + imageCategory.getCategory()))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/available-flash-with-category"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("flashes"));
    }

    @Test
    void viewFrequentlyAskedQuestions_returnsFaqPage() throws Exception {
        when(customerPageTextService.getCustomerPageTextListByPage("frequently-asked-questions"))
                .thenReturn(List.of());

        mockMvc.perform(get("/frequently-asked-questions"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/frequently-asked-questions"))
                .andExpect(model().attributeExists("questions"));
    }

    @Test
    void viewProductCategories_returnsProductCategoriesPage() throws Exception {
        when(productCategoryService.getAllProductCategories()).thenReturn(List.of());
        when(productCategoryService.filterOutProductCategoriesWithoutProducts(Mockito.anyList())).thenReturn(List.of());
        when(productCategoryService.convertProductCategoryListToProductCategoryDtoList(Mockito.anyList())).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/product-categories"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void viewProductsByCategory_displaysProducts_fromCategory() throws Exception {
        ProductCategory productCategory = ProductCategory.builder()
                .name("testCategory")
                .products(List.of(new Product()))
                .build();
        List<ProductCustomerViewDto> productDTOs =
                List.of(new ProductCustomerViewDto());

        when(productCategoryService.getProductCategoryByName("testCategory")).thenReturn(productCategory);
        when(productService.convertProductListToProductCustomerViewDtoList(
                productCategory.getProducts())).thenReturn(productDTOs);

        mockMvc.perform(get("/products/" + productCategory.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/products-with-category"))
                .andExpect(model().attribute("products", productDTOs))
                .andExpect(model().attribute("category", productCategory.getName()));
    }
}
