package com.example.demo.repos;

import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findByDate(LocalDate date);
    List<Booking> findByDateBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime);
    Optional<Booking> findByCustomerAndDate(Customer customer, LocalDateTime dateTime);
}
