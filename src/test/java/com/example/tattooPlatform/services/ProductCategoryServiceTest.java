package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dto.productcategory.ProductCategoryDto;
import com.example.tattooPlatform.model.Product;
import com.example.tattooPlatform.model.ProductCategory;
import com.example.tattooPlatform.repos.ProductCategoryRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class ProductCategoryServiceTest {
    @Autowired
    ProductCategoryRepo productCategoryRepo;
    @Autowired
    ProductCategoryService productCategoryService;

    @AfterEach
    void deleteAll(){
        productCategoryRepo.deleteAll();
    }
    @Test
    void saveProductCategory_shouldSaveProductCategory() {
        productCategoryService.saveProductCategory(new ProductCategory());

        assertEquals(1, productCategoryRepo.findAll().size());
    }

    @Test
    void deleteProductCategory_shouldDeleteProductCategory() {
        ProductCategory productCategory = new ProductCategory();
        productCategoryRepo.save(productCategory);
        productCategoryService.deleteProductCategory(productCategory);

        assertTrue(productCategoryRepo.findAll().isEmpty());
    }

    @Test
    void getAllProductCategories_shouldGetAllProductCategories() {
        List<ProductCategory> productCategories = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            productCategories.add(new ProductCategory());
        }
        productCategoryRepo.saveAll(productCategories);

        List<ProductCategory> foundProductCategories = productCategoryService.getAllProductCategories();

        assertEquals(productCategories.size(), foundProductCategories.size());
    }

    @Test
    void getProductCategoryByName_shouldGetCorrectProductCategory() {
        String name = "test";
        ProductCategory productCategory = ProductCategory.builder()
                .name(name)
                .build();

        productCategoryRepo.save(productCategory);

        assertEquals(name, productCategoryService.getProductCategoryByName(name).getName());
    }

    @Test
    void convertProductCategoryToProductCategoryDto_shouldConvertCorrectly() {
        String name = "test";
        ProductCategory productCategory = ProductCategory.builder()
                .name(name)
                .build();

        assertEquals(name,
                productCategoryService.convertProductCategoryToProductCategoryDto(productCategory).getName());
    }

    @Test
    void convertProductCategoryListToProductCategoryDtoList_shouldConvertCorrectly() {
        String nameOne = "test1";
        ProductCategory productCategoryOne = ProductCategory.builder()
                .name(nameOne)
                .build();
        String nameTwo = "test2";
        ProductCategory productCategoryTwo = ProductCategory.builder()
                .name(nameTwo)
                .build();

        List<ProductCategoryDto> dtoList =
                productCategoryService.convertProductCategoryListToProductCategoryDtoList(
                List.of(productCategoryOne, productCategoryTwo));
        assertEquals(nameOne, dtoList.getFirst().getName());
        assertEquals(nameTwo, dtoList.getLast().getName());
    }

    @Test
    void filterOutProductCategoriesWithoutProducts_shouldPersistProductCategoryWithProduct() {
        ProductCategory productCategory = ProductCategory.builder()
                .products(List.of(new Product()))
                .build();

        assertTrue(productCategoryService.filterOutProductCategoriesWithoutProducts(
                List.of(productCategory)).contains(productCategory));
    }

    @Test
    void filterOutProductCategoriesWithoutProducts_shouldRemoveProductCategoryWithoutProduct() {
        ProductCategory productCategory = new ProductCategory();

        assertFalse(productCategoryService.filterOutProductCategoriesWithoutProducts(
                List.of(productCategory)).contains(productCategory));
    }

    @Test
    void filterOutProductCategoriesWithoutProducts_shouldFilterMultipleProductCategoriesCorrectly() {
        ProductCategory productCategoryOne = ProductCategory.builder()
                .products(List.of(new Product()))
                .build();
        ProductCategory productCategoryTwo = new ProductCategory();

        List<ProductCategory> filteredList = productCategoryService.filterOutProductCategoriesWithoutProducts(
                List.of(productCategoryOne, productCategoryTwo));

        assertTrue(filteredList.contains(productCategoryOne));
        assertFalse(filteredList.contains(productCategoryTwo));
    }
}