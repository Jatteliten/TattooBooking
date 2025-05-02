package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.ImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageCategoryRepo extends JpaRepository<ImageCategory, UUID> {
    ImageCategory findByCategory(String category);
}
