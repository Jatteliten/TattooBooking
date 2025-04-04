package com.example.demo.services;

import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.dtos.bookingdtos.BookingWithoutIdDto;
import com.example.demo.repos.BookingRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepo bookingRepo;

    public BookingService(BookingRepo bookingRepo){
        this.bookingRepo = bookingRepo;
    }

    public List<Booking> getBookingsByDate(LocalDate date){
        LocalDateTime startOfDate = date.atStartOfDay();
        LocalDateTime endOfDate = date.atTime(23, 59, 59);
        return bookingRepo.findByDateBetween(startOfDate, endOfDate);
    }

    public void saveBooking(Booking booking){
        bookingRepo.save(booking);
    }

    public List<Booking> getAllBookings(){
        return bookingRepo.findAll();
    }

    public void deleteBooking(Booking booking){
        bookingRepo.delete(booking);
    }

    public void deleteBookings(List<Booking> bookings){
        bookingRepo.deleteAll(bookings);
    }

    public void deleteAllBookings(){
        bookingRepo.deleteAll();
    }

    public Booking getBookingByCustomerAndDate(Customer customer, LocalDateTime dateTime) {
        return bookingRepo.findByCustomerAndDate(customer, dateTime).orElse(null);
    }

    public List<Booking> getBookingsFromTodayToFourWeeksForward() {
        LocalDateTime fromDateTime = LocalDateTime.now();
        LocalDateTime toDateTime = fromDateTime.plusWeeks(4);
        return bookingRepo.findByDateBetween(fromDateTime, toDateTime);
    }

    public List<Booking> getBookingsBetweenTwoGivenDates(LocalDateTime fromDate, LocalDateTime toDate){
        return bookingRepo.findByDateBetween(fromDate, toDate);
    }

    public BookingCustomerDepositTimeDto convertBookingToBookingCustomerDepositTimeDto(Booking booking){
        return BookingCustomerDepositTimeDto.builder()
                .depositPaid(booking.isDepositPaid())
                .customer(booking.getCustomer())
                .date(booking.getDate())
                .build();
    }

    public BookingWithoutIdDto convertBookingToBookingWithoutIdDto(Booking booking){
        return BookingWithoutIdDto.builder()
                .depositPaid(booking.isDepositPaid())
                .touchUp(booking.isTouchUp())
                .finalPrice(booking.getFinalPrice())
                .date(booking.getDate())
                .notes(booking.getNotes())
                .tattooImage(booking.getTattooImage())
                .customer(booking.getCustomer())
                .build();
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
