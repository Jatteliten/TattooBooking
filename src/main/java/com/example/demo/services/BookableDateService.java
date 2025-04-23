package com.example.demo.services;

import com.example.demo.dtos.bookabledatedtos.DateEntry;
import com.example.demo.dtos.bookabledatedtos.DateForm;
import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.dtos.bokablehourdtos.BookableHourForCalendarDto;
import com.example.demo.dtos.bookabledatedtos.BookableDateForCalendarDto;
import com.example.demo.model.Booking;
import com.example.demo.repos.BookableDateRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BookableDateService {

    private final BookableDateRepo bookableDateRepo;
    private final BookableHourService bookableHourService;

    @Value("${dropin.hour}")
    private static int DROP_IN_START_HOUR;

    @Value("${dropin.minute}")
    private static int DROP_IN_START_MINUTE;

    public BookableDateService(BookableDateRepo bookableDateRepo, BookableHourService bookableHourService){
        this.bookableDateRepo = bookableDateRepo;
        this.bookableHourService = bookableHourService;
    }

    public void saveBookableDate(BookableDate bookableDate){
        if(checkIfBookableHoursInBookableDateAreAllBooked(bookableDate)){
            bookableDate.setFullyBooked(true);
        }
        bookableDateRepo.save(bookableDate);
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

    public void saveListOfBookableDates(List<BookableDate> dateList){
        bookableDateRepo.saveAll(dateList);
    }

    public BookableDate getBookableDateByDate(LocalDate date){
        BookableDate bookableDate = bookableDateRepo.findByDate(date);
        if(bookableDate != null && !bookableDate.getBookableHours().isEmpty()){
            bookableDate.getBookableHours().sort(Comparator.comparing(BookableHour::getHour));
        }
        return bookableDate;
    }

    public void setBookableDateToFullyBookedIfAllHoursAreBooked(BookableDate bookabledate){
        if(bookabledate.getBookableHours().stream().filter(BookableHour::isBooked).toList().size() ==
                bookabledate.getBookableHours().size()){
            bookabledate.setFullyBooked(true);
            saveBookableDate(bookabledate);
        }
    }

    public void setBookableDateAndHoursToUnavailable(BookableDate bookableDate){
        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            if(!bookableHour.isBooked()){
                bookableHour.setBooked(true);
            }
        }
        bookableDate.setFullyBooked(true);
        saveBookableDate(bookableDate);
    }

    public void setBookableDateAndHoursToAvailableIfAllHoursAreNotFullyBooked(BookableDate bookableDate, List<Booking> bookingsAtDate){
        boolean fullyBooked = true;

        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            if(bookingsAtDate.stream().filter(b ->
                            LocalTime.from(b.getDate()).minusMinutes(1).isBefore(bookableHour.getHour())
                                    && LocalTime.from(b.getEndTime()).plusMinutes(1).isAfter(bookableHour.getHour()))
                            .toList()
                            .isEmpty()){
                bookableHour.setBooked(false);
                fullyBooked = false;
            }
        }
        bookableDate.setFullyBooked(fullyBooked);
        saveBookableDate(bookableDate);
    }

    public boolean checkIfBookableHoursInBookableDateAreAllBooked(BookableDate bookableDate){
        return bookableDate.getBookableHours().stream().filter(bh -> !bh.isBooked()).toList().isEmpty();
    }

    public List<BookableDate> createBookableDatesFromDateForm(DateForm dateForm) {
        List<BookableDate> bookableDatesToSaveList = new ArrayList<>();
        for (DateEntry entry : dateForm.getDateList()) {
            BookableDate bookableDate = BookableDate.builder()
                    .date(entry.getDate())
                    .fullyBooked(false)
                    .build();

            if(entry.getType() != null){
                bookableDate.setTouchUp(entry.getType().equals("touchup"));
                bookableDate.setDropIn(entry.getType().equals("dropin"));
            }else{
                bookableDate.setTouchUp(false);
                bookableDate.setDropIn(false);
            }

            if(!bookableDate.isDropIn() && entry.getHours() != null){
                bookableDate.setBookableHours(entry.getHours().stream()
                        .map(hour -> BookableHour.builder()
                                .hour(hour)
                                .booked(false)
                                .build())
                        .toList());
            }else if(bookableDate.isDropIn()){
                bookableDate.setBookableHours(List.of(BookableHour.builder()
                        .hour(LocalTime.of(DROP_IN_START_HOUR, DROP_IN_START_MINUTE))
                        .build()));
            }

            if(bookableDate.getBookableHours() != null){
                bookableDatesToSaveList.add(bookableDate);
            }
        }
        return bookableDatesToSaveList;
    }

    public List<LocalDate> getAvailableDatesBetweenTwoDates(LocalDate from, LocalDate to) {
        List<LocalDate> availableDates = new ArrayList<>();
        List<BookableDate> alreadyExistingBookableDateList =
                findBookableDatesBetweenTwoGivenDates(from, to);

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

    public boolean checkIfHourIsAvailable(LocalTime hour, List<Booking> bookings) {
        return bookings.stream().filter(booking ->
                booking.getDate().toLocalTime().isBefore(hour) &&
                        booking.getEndTime().toLocalTime().isAfter(hour)).toList().isEmpty();
    }

    public boolean checkIfBookableHourExistsAtBookableDate(BookableDate bookableDate, LocalTime hour){
        return !bookableDate.getBookableHours()
                .stream().filter(bookableHour -> bookableHour.getHour().equals(hour)).toList().isEmpty();
    }

    public void addHourToBookableDate(BookableDate bookableDate, LocalTime hour){
        bookableDate.getBookableHours().add(BookableHour.builder()
                .hour(hour)
                .booked(false)
                .date(bookableDate)
                .build());
        bookableDate.setFullyBooked(false);
        saveBookableDate(bookableDate);
    }

    public void setBookableDateAndBookableHourToAvailable(BookableDate bookableDate, BookableHour bookableHour){
        bookableHour.setBooked(false);
        bookableDate.setFullyBooked(false);
        saveBookableDate(bookableDate);
    }

}
