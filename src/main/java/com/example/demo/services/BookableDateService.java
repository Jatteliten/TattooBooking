package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.repos.BookableDateRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookableDateService {

    private final BookableDateRepo bookableDateRepo;

    public BookableDateService(BookableDateRepo bookableDateRepo){
        this.bookableDateRepo = bookableDateRepo;
    }

    public void saveBookableDate(BookableDate bookableDate){
        bookableDateRepo.save(bookableDate);
    }

    @Transactional
    public void saveAllBookableDatesAndAssociatedHours(List<BookableDate> dateList){
        bookableDateRepo.saveAll(dateList);
    }

    public List<BookableDate> getAllCurrentlyAvailableBookableDates(){
        return bookableDateRepo.findByFullyBookedFalse();
    }

    public List<BookableDate> sortBookableDateListByDate(List<BookableDate> listToSort){
        listToSort.sort(Comparator.comparing(BookableDate::getDate));
        return listToSort;
    }

    public BookableDate findBookableDateByDate(LocalDate date){
        return bookableDateRepo.findByDate(date);
    }
}
