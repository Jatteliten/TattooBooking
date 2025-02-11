package com.example.demo.services;

import com.example.demo.model.Customer;
import com.example.demo.repos.CustomerRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepo customerRepo;

    public CustomerService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    public List<Customer> getAllCustomers(){
        return customerRepo.findAll();
    }

    public void saveCustomer(Customer customer){
        customerRepo.save(customer);
    }
    public void deleteAllCustomers(){
        customerRepo.deleteAll();
    }

    public Customer findCustomerIfAtLeastOneContactMethodMatches(Customer customer) {
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            Customer foundCustomer = findCustomerByAnyField(customer.getPhone());
            if (foundCustomer != null){
                return foundCustomer;
            }
        }
        if (customer.getInstagram() != null && !customer.getInstagram().isEmpty()) {
            Customer foundCustomer = findCustomerByAnyField(customer.getInstagram());
            if (foundCustomer != null){
                return foundCustomer;
            }
        }
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            return findCustomerByAnyField(customer.getEmail());
        }
        return null;
    }

    public Customer findCustomerByAnyField(String input) {
        return customerRepo.findByAnyContactMethod(input).orElse(null);
    }
}
