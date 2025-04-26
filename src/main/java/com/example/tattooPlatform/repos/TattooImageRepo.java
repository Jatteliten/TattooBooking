package com.example.tattooPlatform.repos;

import com.example.tattooPlatform.model.TattooImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TattooImageRepo extends JpaRepository<TattooImage, UUID> { }
