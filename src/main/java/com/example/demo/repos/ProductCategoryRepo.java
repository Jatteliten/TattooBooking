package com.example.demo.repos;

import com.example.demo.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductCategoryRepo extends JpaRepository<ProductCategory, UUID> {
    ProductCategory findByName(String name);
}