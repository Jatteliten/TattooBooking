package com.example.tattooPlatform.dtos.bookabledatedtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookableDateForCalendarDto {
    private LocalDate date;
    private List<String> hours;
    private boolean bookable;
    private boolean currentMonth;
    private boolean fullyBooked;
    private boolean touchUp;
    private boolean dropIn;
}
