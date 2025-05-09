package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.BookableDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookableDateRepo extends JpaRepository<BookableDate, UUID> {
    BookableDate findByDate(LocalDate date);
    List<BookableDate> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
