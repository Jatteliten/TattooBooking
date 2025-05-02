package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.productcategory.ProductCategoryDto;
import com.example.tattooplatform.model.ProductCategory;
import com.example.tattooplatform.repos.ProductCategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepo productCategoryRepo;

    public ProductCategoryService(ProductCategoryRepo productCategoryRepo) {
        this.productCategoryRepo = productCategoryRepo;
    }

    @Transactional
    public void saveProductCategory(ProductCategory category) {
        productCategoryRepo.saveAndFlush(category);
    }

    public void deleteProductCategory(ProductCategory productCategory){
        productCategoryRepo.delete(productCategory);
    }

    public List<ProductCategory> getAllProductCategories(){
        return productCategoryRepo.findAll();
    }

    public ProductCategory getProductCategoryByName(String name){
        return productCategoryRepo.findByName(name);
    }

    public ProductCategoryDto convertProductCategoryToProductCategoryDto(
            ProductCategory productcategory){
        return ProductCategoryDto.builder().name(productcategory.getName()).build();
    }

    public List<ProductCategoryDto> convertProductCategoryListToProductCategoryDtoList(
            List<ProductCategory> productCategories){
        return productCategories.stream()
                .map(this::convertProductCategoryToProductCategoryDto).toList();
    }

    public List<ProductCategory> filterOutProductCategoriesWithoutProducts(List<ProductCategory> productCategories){
        return productCategories.stream().filter(productCategory ->
                productCategory.getProducts() != null && !productCategory.getProducts().isEmpty())
                .toList();
    }
}
