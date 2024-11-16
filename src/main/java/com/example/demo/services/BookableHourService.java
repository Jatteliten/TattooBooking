package com.example.demo.services;

import com.example.demo.model.BookableHour;
import com.example.demo.repos.BookableHourRepo;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class BookableHourService {
    private final BookableHourRepo bookableHourRepo;

    public BookableHourService(BookableHourRepo bookableHourRepo){
        this.bookableHourRepo = bookableHourRepo;
    }

    public void saveBookableHour(BookableHour bookableHour){
        bookableHourRepo.save(bookableHour);
    }

    public BookableHour getBookableHourByHour(LocalTime hour){
        return bookableHourRepo.findByHour(hour);
    }

    public BookableHour findBookableHourOrCreateBookableHourIfItDoesNotAlreadyExist(LocalTime hour){
        BookableHour bookableHour = getBookableHourByHour(hour);
        if(bookableHour != null){
            return bookableHour;
        }
        BookableHour bookableHourToSave = BookableHour.builder().hour(hour).build();
        saveBookableHour(bookableHourToSave);
        return bookableHourToSave;
    }
}
