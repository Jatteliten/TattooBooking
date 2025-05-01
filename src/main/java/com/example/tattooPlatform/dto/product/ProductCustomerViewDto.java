package com.example.tattooPlatform.dto.product;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCustomerViewDto {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
}
