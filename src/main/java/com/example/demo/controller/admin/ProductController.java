package com.example.demo.controller.admin;

import com.example.demo.model.Product;
import com.example.demo.model.ProductCategory;
import com.example.demo.services.ProductCategoryService;
import com.example.demo.services.ProductService;
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
        name = name.trim().toLowerCase();
        if(productCategoryService.getProductCategoryByName(name) != null){
            model.addAttribute("error", "Category already exists.");
        }else{
            productCategoryService.saveProductCategory(ProductCategory.builder().name(name).build());
            model.addAttribute("success", "Category " + name + " added!");
        }

        return populateProductCategoriesAndReturnManageProductCategories(model);
    }

    @PostMapping("/delete-category")
    public String deleteCategory(@RequestParam String name, Model model){
        name = name.trim().toLowerCase();
        ProductCategory productCategory = productCategoryService.getProductCategoryByName(name);

        if(productCategory == null){
            model.addAttribute("error", "Category " + name + " does not exist.");
        }else if(productCategory.getProducts().isEmpty()){
            productCategoryService.deleteProductCategory(productCategory);
            model.addAttribute("success", "Category " + name + " deleted.");
        }else{
            model.addAttribute("error", "Category " + name + " still has products associated with it.");
        }

        return populateProductCategoriesAndReturnManageProductCategories(model);
    }

    @GetMapping("/products-by-category")
    public String viewProductsByCategory(@RequestParam String category, Model model){
        model.addAttribute("category", productCategoryService.getProductCategoryByName(category));
        return "/admin/manage-products-by-category";
    }

    @PostMapping("/add-product")
    public String saveProduct(@RequestParam String categoryName,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam double price,
                              @RequestParam("file") MultipartFile file,
                              Model model){
        ProductCategory productCategory = productCategoryService.getProductCategoryByName(categoryName);
        Product product = productService.createAndSaveProductWithAllAttributes(productCategory, name, description, price, file);
        if(product == null) {
            model.addAttribute("error", "Could not save product.");
            return populateProductCategoriesAndReturnManageProductCategories(model);
        }else{
            productCategory.getProducts().add(product);
            productCategoryService.saveProductCategory(productCategory);
            model.addAttribute("success", name + " saved!");
        }

        model.addAttribute("category", productCategory);
        return "/admin/manage-products-by-category";
    }

    @PostMapping("/change-product-description")
    public String changeProductDescription(@RequestParam UUID id, String description, Model model){
        Product product = productService.getProductById(id);
        product.setDescription(description);
        productService.saveProduct(product);

        model.addAttribute("success", product.getName() + " description changed.");
        model.addAttribute("category", product.getCategory());
        return "/admin/manage-products-by-category";
    }

    @PostMapping("/change-product-price")
    public String changeProductPrice(@RequestParam UUID id, double price, Model model){
        Product product = productService.getProductById(id);
        product.setPrice(price);
        productService.saveProduct(product);

        model.addAttribute("success", product.getName() + " price changed.");
        model.addAttribute("category", product.getCategory());
        return "/admin/manage-products-by-category";
    }

    @PostMapping("/delete-product")
    public String deleteProduct(@RequestParam UUID id, Model model){
        Product product = productService.getProductById(id);
        if(product == null){
            model.addAttribute("error", "Could not delete product.");
            return populateProductCategoriesAndReturnManageProductCategories(model);

        }else{
            ProductCategory category = product.getCategory();
            category.getProducts().remove(product);
            productCategoryService.saveProductCategory(category);
            String productName = product.getName();
            productService.deleteProduct(productService.getProductById(id));
            model.addAttribute("success", productName + " deleted.");
            model.addAttribute("category", category);
            return "/admin/manage-products-by-category";
        }
    }

    private String populateProductCategoriesAndReturnManageProductCategories(Model model){
        model.addAttribute("categories", productCategoryService.getAllProductCategories());
        return "/admin/manage-product-categories";
    }

}
