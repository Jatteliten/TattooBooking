package com.example.demo.model.dtos.bookingdtos;

import com.example.demo.model.Customer;
import com.example.demo.model.TattooImage;
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
    private boolean touchUpMade;
    private int finalPrice;
    private LocalDateTime date;
    private String notes;
    private TattooImage tattooImage;
    private Customer customer;
}
