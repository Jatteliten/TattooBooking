package com.example.tattooPlatform.dtos.productdtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductWithNameDescriptionPriceImageUrlDto {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
}
