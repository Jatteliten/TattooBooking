package com.example.tattooPlatform.repos;

import com.example.tattooPlatform.model.ImageCategory;
import com.example.tattooPlatform.model.TattooImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TattooImageRepo extends JpaRepository<TattooImage, UUID> {
    Page<TattooImage> findByCategoriesInOrderByBookingDateDesc(List<ImageCategory> categories, Pageable pageable);

    int countByCategoriesIn(List<ImageCategory> categories);

}
