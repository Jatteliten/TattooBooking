package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class ImageCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    private String category;

    @ManyToMany
    private List<TattooImage> tattooImages;

    @ManyToMany
    private List<FlashImage> flashImages;
}
