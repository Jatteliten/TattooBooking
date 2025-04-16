package com.example.demo.repos;

import com.example.demo.model.InstagramEmbed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstagramEmbedRepo extends JpaRepository<InstagramEmbed, UUID> {

}
