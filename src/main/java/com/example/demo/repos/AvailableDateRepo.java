package com.example.demo.repos;

import com.example.demo.model.AvailableDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableDateRepo extends JpaRepository<AvailableDate, Long> {
}
