package com.example.tattooplatform.controller.admin;

import com.example.tattooplatform.model.Product;
import com.example.tattooplatform.model.ProductCategory;
import com.example.tattooplatform.services.ProductCategoryService;
import com.example.tattooplatform.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasAuthority('Admin')")
public class ProductController {
    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private static final String CATEGORY = "category";
    private static final String MANAGE_PRODUCTS_BY_CATEGORY_TEMPLATE = "/admin/manage-products-by-category";

    public ProductController(ProductService productService, ProductCategoryService productCategoryService) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
    }

    @GetMapping("/")
    public String manageProductCategories(Model model){
        return populateProductCategoriesAndReturnManageProductCategories(model);
    }

    @PostMapping("/add-category")
    public String saveCategory(@RequestParam String name, Model model){
        name = name.trim().substring(0, 1).toUpperCase() + name.trim().substring(1).toLowerCase();
        if(productCategoryService.getProductCategoryByName(name) != null){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Category already exists.");
        }else{
            productCategoryService.saveProductCategory(ProductCategory.builder().name(name).build());
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), generateCategoryUpdateFeedback(name, "added!"));
        }

        return populateProductCategoriesAndReturnManageProductCategories(model);
    }

    @PostMapping("/delete-category")
    public String deleteCategory(@RequestParam String name, Model model){
        ProductCategory productCategory = productCategoryService.getProductCategoryByName(name);

        if(productCategory == null){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), generateCategoryUpdateFeedback(name, "does not exist."));
        }else if(productCategory.getProducts().isEmpty()){
            productCategoryService.deleteProductCategory(productCategory);
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), generateCategoryUpdateFeedback(name, "deleted."));
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), generateCategoryUpdateFeedback(name, "still has products associated with it."));
        }

        return populateProductCategoriesAndReturnManageProductCategories(model);
    }

    @GetMapping("/products-by-category")
    public String viewProductsByCategory(@RequestParam String category, Model model){
        model.addAttribute(CATEGORY, productCategoryService.getProductCategoryByName(category));
        return MANAGE_PRODUCTS_BY_CATEGORY_TEMPLATE;
    }

    @PostMapping("/add-product")
    public String saveProduct(@RequestParam String categoryName,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam double price,
                              @RequestParam("file") MultipartFile file,
                              Model model){
        name = name.trim().substring(0, 1).toUpperCase() + name.trim().substring(1).toLowerCase();
        ProductCategory productCategory = productCategoryService.getProductCategoryByName(categoryName);
        Product product = productService.createAndSaveProductWithAllAttributes(productCategory, name, description, price, file);
        if(product == null) {
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Could not save product.");
            return populateProductCategoriesAndReturnManageProductCategories(model);
        }else{
            productCategory.getProducts().add(product);
            productCategoryService.saveProductCategory(productCategory);
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), name + " saved!");
        }

        model.addAttribute(CATEGORY, productCategory);
        return MANAGE_PRODUCTS_BY_CATEGORY_TEMPLATE;
    }

    @PostMapping("/change-product-description")
    public String changeProductDescription(@RequestParam UUID id, String description, Model model){
        Product product = productService.getProductById(id);
        product.setDescription(description);
        productService.saveProduct(product);

        model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), product.getName() + " description changed.");
        model.addAttribute(CATEGORY, product.getCategory());
        return MANAGE_PRODUCTS_BY_CATEGORY_TEMPLATE;
    }

    @PostMapping("/change-product-price")
    public String changeProductPrice(@RequestParam UUID id, double price, Model model){
        Product product = productService.getProductById(id);
        product.setPrice(price);
        productService.saveProduct(product);

        model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), product.getName() + " price changed.");
        model.addAttribute(CATEGORY, product.getCategory());
        return MANAGE_PRODUCTS_BY_CATEGORY_TEMPLATE;
    }

    @PostMapping("/delete-product")
    public String deleteProduct(@RequestParam UUID id, Model model){
        Product product = productService.getProductById(id);
        if(product == null){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Could not delete product.");
            return populateProductCategoriesAndReturnManageProductCategories(model);
        }else{
            ProductCategory category = product.getCategory();
            category.getProducts().remove(product);
            productCategoryService.saveProductCategory(category);
            String productName = product.getName();
            productService.deleteProduct(productService.getProductById(id));
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), productName + " deleted.");
            model.addAttribute(CATEGORY, category);
            return MANAGE_PRODUCTS_BY_CATEGORY_TEMPLATE;
        }
    }

    private String populateProductCategoriesAndReturnManageProductCategories(Model model){
        model.addAttribute("categories", productCategoryService.getAllProductCategories());
        return "/admin/manage-product-categories";
    }

    private String generateCategoryUpdateFeedback(String name, String feedback){
        return "Category " + name + " " + feedback;
    }

}
