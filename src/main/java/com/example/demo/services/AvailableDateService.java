package com.example.demo.services;

import com.example.demo.model.AvailableDate;
import com.example.demo.repos.AvailableDateRepo;
import org.springframework.stereotype.Service;

@Service
public class AvailableDateService {

    AvailableDateRepo availableDateRepo;

    public AvailableDateService(AvailableDateRepo availableDateRepo){
        this.availableDateRepo = availableDateRepo;
    }

    public void saveAvailableDate(AvailableDate availableDate){
        availableDateRepo.save(availableDate);
    }
}
