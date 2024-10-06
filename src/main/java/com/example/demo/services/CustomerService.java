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

    public Customer findCustomerByPhone(String phone) {
        return customerRepo.findByPhone(phone).orElse(null);
    }

    public Customer findCustomerByInstagram(String instagram) {
        return customerRepo.findByInstagram(instagram).orElse(null);
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepo.findByEmail(email).orElse(null);
    }

    public Customer findCustomerByPhoneInstagramOrEmail(String input){
        Customer customer = findCustomerByPhone(input);
        if(customer != null){
            return customer;
        }

        customer = findCustomerByEmail(input);
        if(customer != null){
            return customer;
        }

        return findCustomerByInstagram(input);
    }
}
