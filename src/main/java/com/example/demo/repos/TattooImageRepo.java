package com.example.demo.repos;

import com.example.demo.model.TattooImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TattooImageRepo extends JpaRepository<TattooImage, Long> {
    List<TattooImage> findByFrontPageTrue();
    List<TattooImage> findByFrontPageFalse();
}
