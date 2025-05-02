package com.example.tattooplatform.dto.customerpagetext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPageTextPrioritizedDto {
    private String text;
    private String section;
    private int priority;
}
