package com.example.tattooplatform.dto.customerpagetext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPageTextDto {
    private String text;
    private String section;
}
