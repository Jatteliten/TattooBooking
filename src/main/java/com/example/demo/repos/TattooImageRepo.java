package com.example.demo.repos;

import com.example.demo.model.TattooImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TattooImageRepo extends JpaRepository<TattooImage, UUID> {
    List<TattooImage> findByFrontPageTrue();
    List<TattooImage> findByFrontPageFalse();
    TattooImage findByUrl(String url);
}
