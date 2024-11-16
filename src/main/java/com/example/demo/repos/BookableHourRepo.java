package com.example.demo.repos;

import com.example.demo.model.BookableHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;

public interface BookableHourRepo extends JpaRepository<BookableHour, Long> {
    BookableHour findByHour(LocalTime hour);
}
