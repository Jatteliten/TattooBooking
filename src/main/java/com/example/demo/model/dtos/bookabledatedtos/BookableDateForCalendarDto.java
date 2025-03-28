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
    LocalDate date;
    List<BookableHourForCalendarDto> hours;
    boolean currentMonth;
    boolean fullyBooked;
    boolean dropIn;
}
