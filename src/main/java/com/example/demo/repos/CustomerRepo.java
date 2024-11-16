package com.example.demo.repos;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneContains(String phone);
    Optional<Customer> findByInstagramContains(String instagram);
    Optional<Customer> findByEmailContains(String email);
}
