package com.example.tattooplatform.controller.admin;

import com.example.tattooplatform.controller.ModelFeedback;
import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import com.example.tattooplatform.services.FlashImageService;
import com.example.tattooplatform.services.ImageCategoryService;
import com.example.tattooplatform.services.S3ImageService;
import com.example.tattooplatform.services.TattooImageService;
import org.springframework.data.domain.PageRequest;
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
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/images")
@PreAuthorize("hasAuthority('Admin')")
public class ImageController {
    private final ImageCategoryService imageCategoryService;
    private final FlashImageService flashImageService;
    private final TattooImageService tattooImageService;
    private final S3ImageService s3ImageService;
    private static final String CATEGORIES = "categories";
    private static final String IMAGE_CATEGORIES_TEMPLATE = "admin/image-categories";

    public ImageController(ImageCategoryService imageCategoryService, FlashImageService flashImageService,
                           TattooImageService tattooImageService, S3ImageService s3ImageService){
        this.imageCategoryService = imageCategoryService;
        this.flashImageService = flashImageService;
        this.tattooImageService = tattooImageService;
        this.s3ImageService = s3ImageService;
    }

    @GetMapping("/image-categories")
    public String showImageCategories(Model model){
        model.addAttribute(CATEGORIES,
                imageCategoryService.sortImageCategoriesByName(imageCategoryService.getAllImageCategories()));
        return IMAGE_CATEGORIES_TEMPLATE;
    }

    @PostMapping("/save-image-category")
    public String saveImageCategory(@RequestParam String category, Model model){
        category = category.substring(0, 1).toUpperCase() + category.substring(1);
        if(imageCategoryService.getImageCategoryByCategoryName(category) != null){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Category " + category + " already exists!");
        }else{
            ImageCategory imageCategory = ImageCategory.builder()
                    .category(category)
                    .build();

            if(imageCategory != null){
                imageCategoryService.saveImageCategory(imageCategory);
                model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Category saved: " + category + "!");
            }else{
                model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Something went wrong when saving " + category + "!");
            }
        }

        model.addAttribute(CATEGORIES,
                imageCategoryService.sortImageCategoriesByName(imageCategoryService.getAllImageCategories()));

        return IMAGE_CATEGORIES_TEMPLATE;
    }

    @PostMapping("/delete-image-category")
    public String deleteImageCategory(@RequestParam String category, Model model){
        imageCategoryService.deleteImageCategory(
                imageCategoryService.getImageCategoryByCategoryName(category));
        model.addAttribute(CATEGORIES,
                imageCategoryService.sortImageCategoriesByName(imageCategoryService.getAllImageCategories()));
        model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Category deleted: " + category + "!");
        return IMAGE_CATEGORIES_TEMPLATE;
    }

    @GetMapping("/view-flash-images")
    public String viewFlashImages(Model model){
        return populateFlashImageCategories(model);
    }

    @GetMapping("/view-flash-images-by-category")
    public String viewFlashImagesByCategory(@RequestParam String category,
                                            @RequestParam(defaultValue = "0") int page,
                                            Model model){
        return populateFlashImagesModelByCategory(category, page, model);
    }

    @PostMapping("/upload-flash")
    public String uploadFlashImage(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value="categoryIds", required=false) List<UUID> categoryIds,
                                   Model model) {
        if(categoryIds == null){
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Select at least one category");
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

                model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Flash image uploaded!");
            } catch (Exception e) {
                model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Failed to upload image: " + e.getMessage());
            }
        }

        return populateFlashImageCategories(model);
    }

    @PostMapping("/delete-flash")
    public String deleteFlashImage(@RequestParam UUID flashId, @RequestParam String category, Model model){
        FlashImage flashImage = flashImageService.getFlashImageById(flashId);
        if(flashImage != null){
            String flashImageUrl = flashImage.getUrl();
            flashImageService.deleteFlashImage(flashImage);
            s3ImageService.deleteImage(flashImageUrl);

            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Image deleted!");
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), "Image could not be deleted");
        }

        return populateFlashImagesModelByCategory(category, 0, model);
    }

    @GetMapping("/view-tattoo-images")
    public String viewTattooImages(Model model){
        model.addAttribute(CATEGORIES, imageCategoryService.filterAllImageCategoriesWithTattooImages());
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
                .toList();

        model.addAttribute("currentPage", page);
        model.addAttribute("pages", pages);
        model.addAttribute("amountOfImagesInCategory", amountOfImages);
        model.addAttribute(CATEGORIES, imageCategoryService.filterAllImageCategoriesWithTattooImages());
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("images", tattooImageService.getPageOrderedByLatestBookingDate(
                imageCategoryService.getImageCategoryByCategoryName(categoryName), page, 20));
        return "admin/tattoo-images";
    }

    private String populateFlashImageCategories(Model model){
        List<ImageCategory> imageCategories =
                imageCategoryService.sortImageCategoriesByName(imageCategoryService.getAllImageCategories());
        model.addAttribute(CATEGORIES, imageCategories);
        model.addAttribute("categoriesWithImages",
                imageCategoryService.filterImageCategoriesWithoutFlashImages(imageCategories));
        return "admin/flash-images";
    }

    private String populateFlashImagesModelByCategory(String category, int page, Model model) {
        model.addAttribute("category", category);
        model.addAttribute("flashes", flashImageService.getFlashImagesByCategoryPaginated(
                imageCategoryService.getImageCategoryByCategoryName(category), PageRequest.of(page, 12)));
        return populateFlashImageCategories(model);
    }

}
