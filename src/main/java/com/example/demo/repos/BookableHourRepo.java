package com.example.demo.repos;

import com.example.demo.model.BookableHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookableHourRepo extends JpaRepository<BookableHour, UUID> { }
