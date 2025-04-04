package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.dtos.bokablehourdtos.BookableHourForCalendarDto;
import com.example.demo.dtos.bookabledatedtos.BookableDateForCalendarDto;
import com.example.demo.repos.BookableDateRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BookableDateService {

    private final BookableDateRepo bookableDateRepo;
    private final BookableHourService bookableHourService;

    public BookableDateService(BookableDateRepo bookableDateRepo, BookableHourService bookableHourService){
        this.bookableDateRepo = bookableDateRepo;
        this.bookableHourService = bookableHourService;
    }

    public void saveBookableDate(BookableDate bookableDate){
        bookableDateRepo.save(bookableDate);
    }
    public void deleteAllBookableDates(){
        bookableDateRepo.deleteAll();
    }

    public List<BookableDate> findBookableDatesBetweenTwoGivenDates(LocalDate startDate, LocalDate endDate){
        return bookableDateRepo.findByDateBetween(startDate, endDate);
    }

    public BookableDateForCalendarDto convertBookableDateToBookableDateForCalendarDto(BookableDate bookableDate){
        List<String> bookableHoursStringsForCalendarList = new ArrayList<>();
        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            BookableHourForCalendarDto bookableHourForCalendarDto =
                    bookableHourService.convertBookableHourToBookableHourForCalendarDto(bookableHour);
            bookableHoursStringsForCalendarList.add(bookableHourForCalendarDto.getHour() + "-"
                    + bookableHourForCalendarDto.isBooked());
        }
        Collections.sort(bookableHoursStringsForCalendarList);
        return BookableDateForCalendarDto.builder()
                .date(bookableDate.getDate())
                .bookable(true)
                .hours(bookableHoursStringsForCalendarList)
                .currentMonth(true)
                .fullyBooked(bookableDate.isFullyBooked())
                .dropIn(bookableDate.isDropIn())
                .touchUp(bookableDate.isTouchUp())
                .build();
    }

    public List<BookableDateForCalendarDto> convertListOfBookableDatesToBookableDateForCalendarDto(
            List<BookableDate> bookableDateList){
        return bookableDateList.stream()
                .map(this::convertBookableDateToBookableDateForCalendarDto)
                .toList();
    }

    @Transactional
    public void saveListOfBookableDates(List<BookableDate> dateList){
        bookableDateRepo.saveAll(dateList);
    }

    public BookableDate getBookableDateByDate(LocalDate date){
        BookableDate bookableDate = bookableDateRepo.findByDate(date);
        if(!bookableDate.getBookableHours().isEmpty()){
            bookableDate.getBookableHours().sort(Comparator.comparing(BookableHour::getHour));
        }
        return bookableDateRepo.findByDate(date);
    }

}
