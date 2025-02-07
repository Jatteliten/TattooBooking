package com.example.demo.repos;

import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepo extends JpaRepository<Booking, UUID> {
    List<Booking> findByDateBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime);
    Optional<Booking> findByCustomerAndDate(Customer customer, LocalDateTime dateTime);
    List<Booking> findByDate(LocalDateTime date);
}
