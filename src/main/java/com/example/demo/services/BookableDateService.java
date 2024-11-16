package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.repos.BookableDateRepo;
import org.springframework.stereotype.Service;

@Service
public class BookableDateService {

    private final BookableDateRepo bookableDateRepo;

    public BookableDateService(BookableDateRepo bookableDateRepo){
        this.bookableDateRepo = bookableDateRepo;
    }

    public void saveBookableDate(BookableDate bookableDate){
        bookableDateRepo.save(bookableDate);
    }
}
