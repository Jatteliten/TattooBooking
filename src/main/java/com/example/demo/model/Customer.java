package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    private String name;
    private String instagram;
    private String phone;

    @OneToMany
    private List<Booking> bookings;
}