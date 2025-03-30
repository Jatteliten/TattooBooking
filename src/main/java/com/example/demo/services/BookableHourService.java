package com.example.demo.services;

import com.example.demo.model.BookableHour;
import com.example.demo.dtos.bokablehourdtos.BookableHourForCalendarDto;
import com.example.demo.repos.BookableHourRepo;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class BookableHourService {
    private final BookableHourRepo bookableHourRepo;

    public BookableHourService(BookableHourRepo bookableHourRepo){
        this.bookableHourRepo = bookableHourRepo;
    }

    public void saveBookableHour(BookableHour bookableHour){
        bookableHourRepo.save(bookableHour);
    }

    public void saveAllBookableHours(List<BookableHour> hourList){
        bookableHourRepo.saveAll(hourList);
    }

    public BookableHour getBookableHourByHour(LocalTime hour){
        return bookableHourRepo.findByHour(hour);
    }

    public BookableHourForCalendarDto convertBookableHourToBookableHourForCalendarDto(BookableHour bookableHour){
        return BookableHourForCalendarDto.builder()
                .hour(bookableHour.getHour())
                .booked(bookableHour.isBooked())
                .build();
    }

}
