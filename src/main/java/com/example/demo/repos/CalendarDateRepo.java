package com.example.demo.repos;

import com.example.demo.model.CalendarDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarDateRepo extends JpaRepository<CalendarDate, Long> {
    List<CalendarDate> findByFullyBookedTrue();
    List<CalendarDate> findByFullyBookedFalse();
    List<CalendarDate> findByFullyBookedFalseAndDateBetween(LocalDate fromDate, LocalDate toDate);
    List<CalendarDate> findByFullyBookedTrueAndDateBetween(LocalDate fromDate, LocalDate toDate);
}
