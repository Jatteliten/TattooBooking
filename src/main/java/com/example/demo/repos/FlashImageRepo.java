package com.example.demo.repos;

import com.example.demo.model.FlashImage;
import com.example.demo.model.ImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlashImageRepo extends JpaRepository<FlashImage, UUID> {
    List<FlashImage> findByCategoriesContaining(ImageCategory imageCategory);
}
