package com.example.tattooPlatform.services;

import com.example.tattooPlatform.model.BookableDate;
import com.example.tattooPlatform.model.BookableHour;
import com.example.tattooPlatform.dtos.bokablehourdtos.BookableHourForCalendarDto;
import com.example.tattooPlatform.repos.BookableHourRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class BookableHourService {
    private final BookableHourRepo bookableHourRepo;

    public BookableHourService(BookableHourRepo bookableHourRepo){
        this.bookableHourRepo = bookableHourRepo;
    }

    @Transactional
    public void saveAllBookableHours(List<BookableHour> hourList){
        bookableHourRepo.saveAll(hourList);
    }

    public void setBookableHoursInBookableDateToBookedBetweenStartAndEndTime(
            BookableDate bookableDate, LocalTime startTime, LocalTime endTime){
        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            if(bookableHour.getHour().isAfter(startTime.minusMinutes(1))
                    && bookableHour.getHour().isBefore(endTime)){
                bookableHour.setBooked(true);
            }
        }
    }

    public BookableHourForCalendarDto convertBookableHourToBookableHourForCalendarDto(BookableHour bookableHour){
        return BookableHourForCalendarDto.builder()
                .hour(bookableHour.getHour())
                .booked(bookableHour.isBooked())
                .build();
    }

}
