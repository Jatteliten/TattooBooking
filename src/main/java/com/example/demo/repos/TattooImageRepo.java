package com.example.demo.repos;

import com.example.demo.model.ImageCategory;
import com.example.demo.model.TattooImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TattooImageRepo extends JpaRepository<TattooImage, UUID> {
    TattooImage findByUrl(String url);
    List<TattooImage> findByCategoriesContaining(ImageCategory category);
}
