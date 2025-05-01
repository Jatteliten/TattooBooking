package com.example.tattooPlatform.controller.admin;

import com.example.tattooPlatform.model.FlashImage;
import com.example.tattooPlatform.model.ImageCategory;
import com.example.tattooPlatform.services.FlashImageService;
import com.example.tattooPlatform.services.ImageCategoryService;
import com.example.tattooPlatform.services.S3ImageService;
import com.example.tattooPlatform.services.TattooImageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/images")
@PreAuthorize("hasAuthority('Admin')")
public class ImageController {
    private final ImageCategoryService imageCategoryService;
    private final FlashImageService flashImageService;
    private final TattooImageService tattooImageService;
    private final S3ImageService s3ImageService;

    public ImageController(ImageCategoryService imageCategoryService, FlashImageService flashImageService,
                           TattooImageService tattooImageService, S3ImageService s3ImageService){
        this.imageCategoryService = imageCategoryService;
        this.flashImageService = flashImageService;
        this.tattooImageService = tattooImageService;
        this.s3ImageService = s3ImageService;
    }

    @GetMapping("/image-categories")
    public String showImageCategories(Model model){
        model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        return "admin/image-categories";
    }

    @PostMapping("/save-image-category")
    public String saveImageCategory(@RequestParam String category, Model model){
        if(imageCategoryService.getImageCategoryByCategoryName(category) != null){
            model.addAttribute("errorMessage", "Category " + category + " already exists!");
        }else{
            ImageCategory imageCategory = ImageCategory.builder()
                    .category(category)
                    .build();

            if(imageCategory != null){
                imageCategoryService.saveImageCategory(imageCategory);
                model.addAttribute("successMessage", "Category saved: " + category + "!");
            }else{
                model.addAttribute("errorMessage", "Something went wrong when saving " + category + "!");
            }
        }

        model.addAttribute("categories", imageCategoryService.getAllImageCategories());

        return "admin/image-categories";
    }

    @PostMapping("/delete-image-category")
    public String deleteImageCategory(@RequestParam String category, Model model){
        imageCategoryService.deleteImageCategory(
                imageCategoryService.getImageCategoryByCategoryName(category));
        model.addAttribute("categories", imageCategoryService.getAllImageCategories());
        model.addAttribute("successMessage", "Category deleted: " + category + "!");
        return "admin/image-categories";
    }

    @GetMapping("/view-flash-images")
    public String viewFlashImages(Model model){
        model.addAttribute("categories", imageCategoryService.getAllImageCategoriesWithFlashImages());
        return "admin/flash-images";
    }

    @GetMapping("/view-flash-images-by-category")
    public String viewFlashImagesByCategory(@RequestParam String category, Model model){
        return populateFlashImagesModelByCategory(category, model);
    }

    @PostMapping("/upload-flash")
    public String uploadFlashImage(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value="categoryIds", required=false) List<UUID> categoryIds,
                                   Model model) {
        if(categoryIds == null){
            model.addAttribute("failFeedback", "Select at least one category");
        }else{
            try {
                String imageUrl = s3ImageService.uploadImage(file, "Flashes");

                List<ImageCategory> selectedCategories = imageCategoryService.getCategoriesByIds(categoryIds);

                FlashImage flashImage = FlashImage.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .size(file.getSize())
                        .url(imageUrl)
                        .categories(selectedCategories)
                        .build();

                flashImageService.saveFlashImage(flashImage);

                model.addAttribute("successFeedback", "Flash image uploaded!");
            } catch (Exception e) {
                model.addAttribute("failFeedback", "Failed to upload image: " + e.getMessage());
            }
        }

        model.addAttribute("categories", imageCategoryService.getAllImageCategories());

        return "admin/flash-images";
    }

    @PostMapping("/delete-flash")
    public String deleteFlashImage(@RequestParam UUID flashId, @RequestParam String category, Model model){
        FlashImage flashImage = flashImageService.getFlashImageById(flashId);
        if(flashImage != null){
            String flashImageUrl = flashImage.getUrl();
            flashImageService.deleteFlashImage(flashImage);
            s3ImageService.deleteImage(flashImageUrl);

            model.addAttribute("successFeedback", "Image deleted!");
        }else{
            model.addAttribute("failFeedback", "Image could not be deleted");
        }

        return populateFlashImagesModelByCategory(category, model);
    }

    @GetMapping("/view-tattoo-images")
    public String viewTattooImages(Model model){
        model.addAttribute("categories", imageCategoryService.getAllImageCategoriesWithTattooImages());
        return "admin/tattoo-images";
    }

    @GetMapping("/view-tattoos-by-category")
    public String viewTattooImagesByCategory(@RequestParam String categoryName,
                                   @RequestParam int page,
                                   Model model){
        int amountOfImages = tattooImageService.countTattooImagesByImageCategory(
                imageCategoryService.getImageCategoryByCategoryName(categoryName));
        List<Integer> pages = IntStream.range(0, (amountOfImages + 19) / 20)
                .boxed()
                .collect(Collectors.toList());

        model.addAttribute("currentPage", page);
        model.addAttribute("pages", pages);
        model.addAttribute("amountOfImagesInCategory", amountOfImages);
        model.addAttribute("categories", imageCategoryService.getAllImageCategoriesWithTattooImages());
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("images", tattooImageService.getPageOrderedByLatestBookingDate(
                imageCategoryService.getImageCategoryByCategoryName(categoryName), page, 20));
        return "admin/tattoo-images";
    }

    private String populateFlashImagesModelByCategory(String category, Model model) {
        model.addAttribute("categories", imageCategoryService.getAllImageCategoriesWithFlashImages());
        model.addAttribute("category", category);
        model.addAttribute("flashes", flashImageService.getFlashImagesByCategory(
                imageCategoryService.getImageCategoryByCategoryName(category)));

        return "admin/flash-images";
    }

}
