package com.example.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
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
public class TattooImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String contentType;
    private String url;
    private long size;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "tattoo_image_categories",
            joinColumns = @JoinColumn(name = "tattoo_image_id"),
            inverseJoinColumns = @JoinColumn(name = "categories_id"))
    private List<ImageCategory> categories;

    @OneToOne
    private Booking booking;
}
