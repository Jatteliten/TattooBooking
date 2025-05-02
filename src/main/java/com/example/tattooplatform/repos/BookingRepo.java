package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepo extends JpaRepository<Booking, UUID> {
    List<Booking> findByDateBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime);
    List<Booking> findByEndTimeBetween(LocalDateTime fromDateTime, LocalDateTime toDateTIme);
}
