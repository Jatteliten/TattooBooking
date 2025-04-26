package com.example.tattooPlatform.repos;

import com.example.tattooPlatform.model.BookableHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookableHourRepo extends JpaRepository<BookableHour, UUID> { }
