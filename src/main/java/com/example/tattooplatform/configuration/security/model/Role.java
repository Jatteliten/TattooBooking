package com.example.tattooplatform.configuration.security.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID  )
    private UUID id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    List<User> users;
}
