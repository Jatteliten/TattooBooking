package com.example.demo.repos;

import com.example.demo.model.TattooImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TattooImageRepo extends JpaRepository<TattooImage, UUID> { }
