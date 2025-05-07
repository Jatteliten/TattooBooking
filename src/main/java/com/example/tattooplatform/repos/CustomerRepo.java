package com.example.tattooplatform.repos;

import com.example.tattooplatform.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepo extends JpaRepository<Customer, UUID> {
    @Query("SELECT c FROM Customer c WHERE c.phone ILIKE :input OR c.instagram ILIKE :input OR c.email ILIKE :input")
    Optional<Customer> findByAnyContactMethod(@Param("input") String input);

    List<Customer> findByNameContainingIgnoreCase(String input);
    Customer findByPhone(String phone);
    Customer findByInstagram(String instagram);
    Customer findByEmail(String email);
}
