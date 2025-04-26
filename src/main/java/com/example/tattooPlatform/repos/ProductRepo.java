package com.example.tattooPlatform.repos;

import com.example.tattooPlatform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> { }
