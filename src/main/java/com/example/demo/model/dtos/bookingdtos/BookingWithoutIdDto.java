package com.example.demo.model.dtos.bookingdtos;

import com.example.demo.model.CalendarDate;
import com.example.demo.model.Customer;
import com.example.demo.model.TattooImage;
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
@Builder
public class BookingWithoutIdDto {
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
