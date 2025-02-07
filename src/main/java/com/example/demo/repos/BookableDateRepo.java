package com.example.demo.repos;

import com.example.demo.model.BookableDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookableDateRepo extends JpaRepository<BookableDate, UUID> {
    List<BookableDate> findByFullyBookedFalse();
    BookableDate findByDate(LocalDate date);
}
