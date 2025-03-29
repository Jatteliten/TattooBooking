package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.model.dtos.bokablehourdtos.BookableHourForCalendarDto;
import com.example.demo.model.dtos.bookabledatedtos.BookableDateForCalendarDto;
import com.example.demo.repos.BookableDateRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
        List<BookableHourForCalendarDto> bookableHoursForCalendarList = new ArrayList<>();
        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            bookableHoursForCalendarList.add(
                    bookableHourService.convertBookableHourToBookableHourForCalendarDto(bookableHour));
        }
        return BookableDateForCalendarDto.builder()
                .date(bookableDate.getDate())
                .hours(bookableHoursForCalendarList)
                .currentMonth(true)
                .fullyBooked(bookableDate.isFullyBooked())
                .dropIn(bookableDate.isDropIn())
                .touchUp(bookableDate.isTouchUp())
                .build();
    }

    public List<BookableDateForCalendarDto> convertListOfBookableDatesToBookableDateForCalendarDto(
            List<BookableDate> bookableDateList){
        return bookableDateList.stream().map(this::convertBookableDateToBookableDateForCalendarDto).toList();
    }

    @Transactional
    public void saveAllBookableDatesAndAssociatedHours(List<BookableDate> dateList){
        bookableDateRepo.saveAll(dateList);
    }

    public List<BookableDate> getAllCurrentlyAvailableBookableDates(){
        return bookableDateRepo.findByFullyBookedFalse();
    }

    public BookableDate getBookableDateFromBookableDateListByDate(List<BookableDate> bookableDateList, LocalDate date){
        try {
            return bookableDateList.stream().filter(b -> b.getDate().equals(date)).toList().get(0);
        } catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    public List<BookableDate> sortBookableDateListByDate(List<BookableDate> listToSort){
        listToSort.sort(Comparator.comparing(BookableDate::getDate));
        return listToSort;
    }

    public BookableDate findBookableDateByDate(LocalDate date){
        return bookableDateRepo.findByDate(date);
    }

    public List<BookableDate> sortHoursInBookableHourListByHour(List<BookableDate> bookableDateList){
        for(BookableDate bookableDate: bookableDateList){
            bookableDate.getBookableHours().sort(Comparator.comparing(BookableHour::getHour));
        }
        return bookableDateList;
    }
}
