package com.example.demo.repos;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepo extends JpaRepository<Customer, UUID> {
    @Query("SELECT c FROM Customer c WHERE c.phone = :input OR c.instagram = :input OR c.email = :input")
    Optional<Customer> findByAnyContactMethod(@Param("input") String input);
}
