package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.InstagramEmbed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstagramEmbedRepo extends JpaRepository<InstagramEmbed, UUID> {
    InstagramEmbed findFirstByOrderByCreatedAtDesc();
}