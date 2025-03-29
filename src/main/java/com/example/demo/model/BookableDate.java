package com.example.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class BookableDate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    LocalDate date;
    boolean fullyBooked;
    boolean touchUp;
    boolean dropIn;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<BookableHour> bookableHours;
}
