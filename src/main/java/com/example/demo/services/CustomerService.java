package com.example.demo.services;

import com.example.demo.model.Customer;
import com.example.demo.repos.CustomerRepo;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepo customerRepo;

    public CustomerService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    public Customer findCustomerByPhoneOrInstagram(String phone, String instagram) {
        return customerRepo.findByPhoneOrInstagram(phone, instagram).orElse(null);
    }
}
