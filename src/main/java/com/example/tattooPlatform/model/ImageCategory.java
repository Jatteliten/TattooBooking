package com.example.tattooPlatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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


    @Column(unique = true)
    private String category;

    @ToString.Exclude
    @ManyToMany(mappedBy = "categories")
    private List<TattooImage> tattooImages;

    @ToString.Exclude
    @ManyToMany(mappedBy = "categories")
    private List<FlashImage> flashImages;
}
