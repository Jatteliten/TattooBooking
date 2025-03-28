package com.example.demo.model.dtos.bookabledatedtos;

import com.example.demo.model.dtos.bokablehourdtos.BookableHourForCalendarDto;
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
    private List<BookableHourForCalendarDto> hours;
    private boolean currentMonth;
    private boolean fullyBooked;
    private boolean touchUp;
    private boolean dropIn;
}
