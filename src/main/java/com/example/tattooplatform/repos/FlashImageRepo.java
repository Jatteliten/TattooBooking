package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.FlashImage;
import com.example.tattooplatform.model.ImageCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FlashImageRepo extends JpaRepository<FlashImage, UUID> {
    Page<FlashImage> findByCategoriesContaining(ImageCategory category, Pageable pageable);
}
