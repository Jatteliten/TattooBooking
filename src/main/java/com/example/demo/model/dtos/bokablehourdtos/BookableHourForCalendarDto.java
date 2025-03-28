package com.example.demo.model.dtos.bokablehourdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookableHourForCalendarDto {
    private LocalTime hour;
    private boolean booked;
}
