package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.model.Booking;
import com.example.demo.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.dtos.bookingdtos.BookingWithoutIdDto;
import com.example.demo.repos.BookingRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepo bookingRepo;
    private final BookableDateService bookableDateService;

    public BookingService(BookingRepo bookingRepo, BookableDateService bookableDateService){
        this.bookingRepo = bookingRepo;
        this.bookableDateService = bookableDateService;
    }

    public List<Booking> getBookingsByDate(LocalDate date){
        LocalDateTime startOfDate = date.atStartOfDay();
        LocalDateTime endOfDate = date.atTime(23, 59, 59);
        return bookingRepo.findByDateBetween(startOfDate, endOfDate);
    }

    public Booking getBookingById(UUID id){
        return bookingRepo.findById(id).orElse(null);
    }

    public void saveBooking(Booking booking){
        bookingRepo.save(booking);
    }

    public List<Booking> getAllBookings(){
        return bookingRepo.findAll();
    }

    public void deleteBooking(Booking booking){
        setBookableHoursRelatedToBookingToAvailable(booking);
        bookingRepo.delete(booking);
    }

    private void setBookableHoursRelatedToBookingToAvailable(Booking booking){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(LocalDate.from(booking.getDate()));
        List<BookableHour> bookableHoursOnDate = bookableDate.getBookableHours();
        for(BookableHour bookableHour: bookableHoursOnDate){
            LocalTime hour = bookableHour.getHour();
            LocalTime startTime = booking.getDate().toLocalTime();
            LocalTime endTime = booking.getEndTime().toLocalTime();
            if((hour.isBefore(endTime) && hour.isAfter(startTime)) || hour.equals(startTime)){
                bookableHour.setBooked(false);
                if(bookableDate.isFullyBooked()){
                    bookableDate.setFullyBooked(false);
                }
            }
        }
        bookableDateService.saveBookableDate(bookableDate);
    }

    public void deleteBookings(List<Booking> bookings){
        for(Booking booking: bookings){
            setBookableHoursRelatedToBookingToAvailable(booking);
        }
        bookingRepo.deleteAll(bookings);
    }

    public void deleteAllBookings(){
        bookingRepo.deleteAll();
    }

    public boolean checkIfBookingOverlapsWithAlreadyBookedHours(LocalDateTime startTime, LocalDateTime endTime){
        return !bookingRepo.findByDateBetween(startTime, endTime.minusMinutes(1)).isEmpty() ||
                !bookingRepo.findByEndTimeBetween(startTime.plusMinutes(1), endTime).isEmpty();
    }

    public List<Booking> getBookingsBetweenTwoGivenDates(LocalDateTime fromDate, LocalDateTime toDate){
        return bookingRepo.findByDateBetween(fromDate, toDate);
    }

    public BookingWithoutIdDto convertBookingCustomerDepositTimeDtoToBookingWithoutIdDto(
            BookingCustomerDepositTimeDto givenBookingDto){
        Booking b = bookingRepo.findByCustomerAndDate(
                givenBookingDto.getCustomer(), givenBookingDto.getDate()).orElse(null);

        if(b == null){
            return null;
        }
        return BookingWithoutIdDto.builder()
                .depositPaid(b.isDepositPaid())
                .touchUp(b.isTouchUp())
                .finalPrice(b.getFinalPrice())
                .date(b.getDate())
                .notes(b.getNotes())
                .tattooImage(b.getTattooImage())
                .customer(b.getCustomer())
                .build();
    }

}
