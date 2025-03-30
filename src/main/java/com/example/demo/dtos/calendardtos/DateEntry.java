package com.example.demo.dtos.calendardtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateEntry {
    private LocalDate date;
    private List<LocalTime> hours;
    private String type;
}
