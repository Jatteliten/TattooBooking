package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.BookableHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookableHourRepo extends JpaRepository<BookableHour, UUID> { }
