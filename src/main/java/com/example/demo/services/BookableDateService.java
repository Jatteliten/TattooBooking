package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.repos.BookableDateRepo;
import org.springframework.stereotype.Service;

@Service
public class BookableDateService {

    BookableDateRepo bookableDateRepo;

    public BookableDateService(BookableDateRepo bookableDateRepo){
        this.bookableDateRepo = bookableDateRepo;
    }

    public void saveAvailableDate(BookableDate bookableDate){
        bookableDateRepo.save(bookableDate);
    }
}
