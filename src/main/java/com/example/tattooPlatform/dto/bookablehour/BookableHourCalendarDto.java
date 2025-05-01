package com.example.tattooPlatform.dto.bookablehour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookableHourCalendarDto {
    private LocalTime hour;
    private boolean booked;
}
