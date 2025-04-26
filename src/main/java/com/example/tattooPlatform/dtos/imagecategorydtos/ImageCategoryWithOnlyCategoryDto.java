package com.example.tattooPlatform.dtos.imagecategorydtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageCategoryWithOnlyCategoryDto {
    private String category;
}
