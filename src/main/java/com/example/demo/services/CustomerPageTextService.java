package com.example.demo.services;

import com.example.demo.model.CustomerPageText;
import com.example.demo.repos.CustomerPageTextRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerPageTextService {

    private final CustomerPageTextRepo customerPageTextRepo;

    public CustomerPageTextService (CustomerPageTextRepo customerPageTextRepo){
        this.customerPageTextRepo = customerPageTextRepo;
    }

    public CustomerPageText getCustomerPageTextById(UUID id){
        return customerPageTextRepo.findById(id).orElse(null);
    }

    public void saveCustomerPageText(CustomerPageText customerPageText){
        customerPageText.setCreated(LocalDateTime.now());
        customerPageTextRepo.save(customerPageText);
    }

    public void deleteCustomerPageText(CustomerPageText customerPageText){
        customerPageTextRepo.delete(customerPageText);
    }

    public CustomerPageText getLatestCustomerPageTextByPageAndSection(String page, String section){
        return customerPageTextRepo.findTopByPageAndSectionOrderByCreatedDesc(page, section);
    }

    public List<CustomerPageText> getCustomerPageTextListByPage(String page){
        return customerPageTextRepo.findByPage(page);
    }
}
