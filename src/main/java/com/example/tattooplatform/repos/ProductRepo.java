package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> { }
