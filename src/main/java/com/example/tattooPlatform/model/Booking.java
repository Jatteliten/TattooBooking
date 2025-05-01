package com.example.tattooPlatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    private boolean depositPaid;
    private int finalPrice;
    private LocalDateTime date;
    private LocalDateTime endTime;
    private String notes;
    private boolean touchUp;

    @OneToOne
    private TattooImage tattooImage;

    @ManyToOne
    private Customer customer;

    @OneToOne
    private Booking previousBooking;

}
