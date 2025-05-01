package com.example.tattooPlatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class BookableHour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    @Column(name = "\"hour\"")
    private LocalTime hour;
    private boolean booked;

    @ManyToOne
    private BookableDate date;
}
