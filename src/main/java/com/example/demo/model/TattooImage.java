package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class TattooImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    private String name;
    private String contentType;
    private String url;
    private long size;
    private boolean frontPage;

    @OneToOne
    private Booking booking;
}
