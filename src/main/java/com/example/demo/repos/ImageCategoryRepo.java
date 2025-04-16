package com.example.demo.repos;

import com.example.demo.model.ImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageCategoryRepo extends JpaRepository<ImageCategory, UUID> {
    ImageCategory findByCategory(String category);
}
