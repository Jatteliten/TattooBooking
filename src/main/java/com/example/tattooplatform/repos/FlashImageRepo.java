package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlashImageRepo extends JpaRepository<FlashImage, UUID> {
    List<FlashImage> findByCategoriesContaining(ImageCategory imageCategory);
}
