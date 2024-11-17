package com.example.demo.repos;

import com.example.demo.model.BookableDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookableDateRepo extends JpaRepository<BookableDate, Long> {
    List<BookableDate> findByFullyBookedFalse();
    BookableDate findByDate(LocalDate date);
}
