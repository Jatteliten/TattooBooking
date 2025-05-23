package com.example.tattooplatform.dto.bookabledate;

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
