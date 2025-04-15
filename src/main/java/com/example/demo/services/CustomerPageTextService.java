package com.example.demo.services;

import com.example.demo.model.CustomerPageText;
import com.example.demo.repos.CustomerPageTextRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerPageTextService {

    private final CustomerPageTextRepo customerPageTextRepo;

    public CustomerPageTextService (CustomerPageTextRepo customerPageTextRepo){
        this.customerPageTextRepo = customerPageTextRepo;
    }

    public void saveCustomerPageText(CustomerPageText customerPageText){
        customerPageText.setCreated(LocalDateTime.now());
        customerPageTextRepo.save(customerPageText);
    }

    public CustomerPageText getLatestCustomerPageTextByPageAndSection(String page, String section){
        return customerPageTextRepo.findTopByPageAndSectionOrderByCreatedDesc(page, section);
    }
}
