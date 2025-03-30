package com.example.demo.dtos.bookabledatedtos;

import com.example.demo.dtos.bookabledatedtos.DateEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateForm {
    private List<DateEntry> dateList;
}
