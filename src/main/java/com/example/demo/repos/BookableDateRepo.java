package com.example.demo.repos;

import com.example.demo.model.BookableDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookableDateRepo extends JpaRepository<BookableDate, Long> {
}
