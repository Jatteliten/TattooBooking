package com.example.demo.services;

import com.example.demo.dtos.productcategorydtos.ProductCategoryOnlyName;
import com.example.demo.model.ProductCategory;
import com.example.demo.repos.ProductCategoryRepo;
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

    public ProductCategoryOnlyName convertProductCategoryToProductCategoryOnlyNameDTO(
            ProductCategory productcategory){
        return ProductCategoryOnlyName.builder().name(productcategory.getName()).build();
    }

    public List<ProductCategoryOnlyName> convertProductCategoryListToProductCategoryOnlyNameDTOList(
            List<ProductCategory> productCategories){
        return productCategories.stream()
                .map(this::convertProductCategoryToProductCategoryOnlyNameDTO).toList();
    }

    public List<ProductCategory> filterOutProductCategoriesWithoutProducts(List<ProductCategory> productCategories){
        return productCategories.stream().filter(productCategory ->
                productCategory.getProducts() != null && !productCategory.getProducts().isEmpty())
                .toList();
    }
}
