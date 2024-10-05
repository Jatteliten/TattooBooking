package com.example.demo.model.dtos.bookingdtos;

import com.example.demo.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCustomerDepositTimeDto {
    private boolean depositPaid;
    private LocalDateTime time;
    private Customer customer;
}
