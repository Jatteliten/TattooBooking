package com.example.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class BookableDate {

    @Id
    @GeneratedValue
    private Long id;

    LocalDate date;
    boolean fullyBooked;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<BookableHour> bookableHours;
}
