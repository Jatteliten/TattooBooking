package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.bookabledate.DateEntry;
import com.example.tattooplatform.dto.bookabledate.DateForm;
import com.example.tattooplatform.model.BookableDate;
import com.example.tattooplatform.model.BookableHour;
import com.example.tattooplatform.model.Booking;
import com.example.tattooplatform.repos.BookableDateRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BookableDateService {

    private final BookableDateRepo bookableDateRepo;

    public BookableDateService(BookableDateRepo bookableDateRepo){
        this.bookableDateRepo = bookableDateRepo;
    }

    public void saveBookableDate(BookableDate bookableDate){
        if(checkIfBookableHoursInBookableDateAreAllBooked(bookableDate)){
            bookableDate.setFullyBooked(true);
        }
        bookableDateRepo.save(bookableDate);
    }

    @Transactional
    public void saveListOfBookableDates(List<BookableDate> dateList){
        bookableDateRepo.saveAll(dateList);
    }

    @Transactional
    public void addHourToBookableDate(BookableDate bookableDate, LocalTime hour){
        bookableDate.getBookableHours().add(BookableHour.builder()
                .hour(hour)
                .booked(false)
                .date(bookableDate)
                .build());
        bookableDate.setFullyBooked(false);
        saveBookableDate(bookableDate);
    }

    @Transactional
    public BookableDate getBookableDateByDate(LocalDate date){
        BookableDate bookableDate = bookableDateRepo.findByDate(date);
        if(bookableDate != null && bookableDate.getBookableHours().size() > 1){
            bookableDate.getBookableHours().sort(Comparator.comparing(BookableHour::getHour));
        }
        return bookableDate;
    }

    public List<BookableDate> getBookableDatesBetweenTwoDates(LocalDate startDate, LocalDate endDate){
        return bookableDateRepo.findByDateBetween(startDate, endDate);
    }

    public boolean checkIfBookableHoursInBookableDateAreAllBooked(BookableDate bookableDate){
        return bookableDate.getBookableHours().stream().filter(bh -> !bh.isBooked()).toList().isEmpty();
    }

    public boolean checkIfHourIsAvailable(LocalTime hour, List<Booking> bookings) {
        return bookings.stream().filter(booking ->
                booking.getDate().toLocalTime().isBefore(hour) &&
                        booking.getEndTime().toLocalTime().isAfter(hour)).toList().isEmpty();
    }

    public boolean checkIfBookableHourExistsAtBookableDate(BookableDate bookableDate, LocalTime hour){
        return !bookableDate.getBookableHours()
                .stream().filter(bookableHour -> bookableHour.getHour().equals(hour)).toList().isEmpty();
    }

    @Transactional
    public void setBookableDateAndHoursToUnavailable(BookableDate bookableDate){
        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            if(!bookableHour.isBooked()){
                bookableHour.setBooked(true);
            }
        }
        bookableDate.setFullyBooked(true);
        saveBookableDate(bookableDate);
    }

    @Transactional
    public void setBookableDateAndBookableHourToAvailable(BookableDate bookableDate, BookableHour bookableHour){
        bookableHour.setBooked(false);
        bookableDate.setFullyBooked(false);
        saveBookableDate(bookableDate);
    }

    @Transactional
    public void setBookableDateToFullyBookedIfAllHoursAreBooked(BookableDate bookabledate){
        if(bookabledate.getBookableHours().stream().filter(BookableHour::isBooked).toList().size() ==
                bookabledate.getBookableHours().size()){
            bookabledate.setFullyBooked(true);
            saveBookableDate(bookabledate);
        }
    }

    public void setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(BookableDate bookableDate, List<Booking> bookingsAtDate){
        boolean fullyBooked = true;

        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            if(bookingsAtDate.stream().filter(b ->
                            LocalTime.from(b.getDate()).minusMinutes(1).isBefore(bookableHour.getHour())
                                    && LocalTime.from(b.getEndTime()).isAfter(bookableHour.getHour()))
                            .toList()
                            .isEmpty()){
                bookableHour.setBooked(false);
                fullyBooked = false;
            }
        }
        bookableDate.setFullyBooked(fullyBooked);
    }

    public List<LocalDate> getAvailableDatesBetweenTwoDates(LocalDate from, LocalDate to) {
        List<LocalDate> availableDates = new ArrayList<>();
        List<BookableDate> alreadyExistingBookableDateList = getBookableDatesBetweenTwoDates(from, to);

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            LocalDate currentDateInIteration = date;

            if (date.getDayOfWeek() != DayOfWeek.SUNDAY && alreadyExistingBookableDateList.stream()
                    .filter(bookableDate -> bookableDate.getDate().equals(currentDateInIteration))
                    .toList()
                    .isEmpty()) {
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    public List<BookableDate> createBookableDatesFromDateForm(DateForm dateForm) {
        List<BookableDate> bookableDatesToSaveList = new ArrayList<>();
        for (DateEntry entry : dateForm.getDateList()) {
            BookableDate bookableDate = BookableDate.builder()
                    .date(entry.getDate())
                    .fullyBooked(true)
                    .build();

            if(entry.getType() != null){
                bookableDate.setTouchUp(entry.getType().equals("touchup"));
                bookableDate.setDropIn(entry.getType().equals("dropin"));
            }else{
                bookableDate.setTouchUp(false);
                bookableDate.setDropIn(false);
            }

            if(entry.getHours() != null){
                bookableDate.setBookableHours(entry.getHours().stream()
                        .map(hour -> BookableHour.builder()
                                .hour(hour)
                                .booked(true)
                                .build())
                        .toList());
            }

            if(bookableDate.getBookableHours() != null){
                bookableDatesToSaveList.add(bookableDate);
            }
        }
        return bookableDatesToSaveList;
    }

}
