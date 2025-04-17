package com.example.demo.controller.admin;

import com.example.demo.model.ImageCategory;
import com.example.demo.services.ImageCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/images")
@PreAuthorize("hasAuthority('Admin')")
public class ImageController {

    ImageCategoryService imageCategoryService;

    public ImageController(ImageCategoryService imageCategoryService){
        this.imageCategoryService = imageCategoryService;
    }

    @GetMapping("/image-categories")
    public String showImageCategories(Model model){
        model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        return "admin/image-categories";
    }

    @PostMapping("/save-image-category")
    public String saveImageCategory(@RequestParam String category, Model model){
        if(imageCategoryService.findImageCategoryByCategoryString(category) != null){
            model.addAttribute("categoryError", "Category " + category + " already exists!");
        }else{
            ImageCategory imageCategory = ImageCategory.builder()
                    .category(category)
                    .build();

            if(imageCategory != null){
                imageCategoryService.saveImageCategory(imageCategory);
                model.addAttribute("savedCategory", "Category saved: " + category + "!");
            }else{
                model.addAttribute("categoryError", "Something went wrong when saving " + category + "!");
            }
        }

        model.addAttribute("categories", imageCategoryService.getAllImageCategories());

        return "admin/image-categories";
    }

    @PostMapping("/delete-image-category")
    public String deleteImageCategory(@RequestParam String category, Model model){
        imageCategoryService.deleteImageCategory(
                imageCategoryService.findImageCategoryByCategoryString(category));
        model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        model.addAttribute("deletedCategory", "Category deleted: " + category + "!");
        return "admin/image-categories";
    }

}
