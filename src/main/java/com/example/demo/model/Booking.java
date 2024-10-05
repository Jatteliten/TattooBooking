package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    private boolean depositPaid;
    private int finalPrice;
    private LocalDateTime time;

    @OneToOne
    private TattooImage tattooImage;

    @ManyToOne
    private CalendarDate date;

    @ManyToOne
    private Customer customer;

}
