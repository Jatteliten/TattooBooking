package com.example.demo.model.dtos.tattooImagesdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TattooImageUrlDto {
    private String url;
    private String name;
}
