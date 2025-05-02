package com.example.tattooplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class CustomerPageText {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    private String text;
    private String page;
    private String section;
    private Integer priority;
    private LocalDateTime created;
}
