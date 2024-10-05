package com.example.demo.services;

import com.example.demo.model.CalendarDate;
import com.example.demo.repos.CalendarDateRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarDateService {

    CalendarDateRepo calendarDateRepo;

    public CalendarDateService(CalendarDateRepo calendarDateRepo){
        this.calendarDateRepo = calendarDateRepo;
    }

    public List<CalendarDate> getAllCalendarDates(){
        return calendarDateRepo.findAll();
    }

    public List<CalendarDate> findAllCalendarDatesThatAreNotFullyBooked(){
        return calendarDateRepo.findByFullyBookedFalse();
    }

    public List<CalendarDate> findAllCalendarDatesThatAreFullyBooked(){
        return calendarDateRepo.findByFullyBookedTrue();
    }

    public List<CalendarDate> findAllCalendarDatesThatAreNotFullyBookedBetweenTwoDates(LocalDate fromDate, LocalDate toDate) {
        return calendarDateRepo.findByFullyBookedFalseAndDateBetween(fromDate, toDate);
    }

    public List<CalendarDate> findAllCalendarDatesThatAreFullyBookedBetweenTwoDates(LocalDate fromDate, LocalDate toDate) {
        return calendarDateRepo.findByFullyBookedTrueAndDateBetween(fromDate, toDate);
    }

}
