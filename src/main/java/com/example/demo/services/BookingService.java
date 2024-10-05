package com.example.demo.services;

import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.model.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.model.dtos.bookingdtos.BookingWithoutIdDto;
import com.example.demo.repos.BookingRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    BookingRepo bookingRepo;

    public BookingService(BookingRepo bookingRepo){
        this.bookingRepo = bookingRepo;
    }

    public Booking getBookingByCustomerAndDate(Customer customer, LocalDateTime dateTime) {
        return bookingRepo.findByCustomerAndDate(customer, dateTime).orElse(null);
    }

    public List<Booking> getBookingsFromThisDateToFourWeeksForward() {
        LocalDateTime fromDateTime = LocalDateTime.now();
        LocalDateTime toDateTime = fromDateTime.plusWeeks(4);
        return bookingRepo.findByDateBetween(fromDateTime, toDateTime);
    }

    public BookingCustomerDepositTimeDto convertBookingToBookingCustomerDepositTimeDto(Booking booking){
        return BookingCustomerDepositTimeDto.builder()
                .depositPaid(booking.isDepositPaid())
                .customer(booking.getCustomer())
                .time(booking.getTime())
                .build();
    }

    public BookingWithoutIdDto convertBookingCustomerDepositTimeDtoToBookingWithoutIdDto(
            BookingCustomerDepositTimeDto booking){
        Booking b = bookingRepo.findByCustomerAndDate(booking.getCustomer(), booking.getTime()).orElse(null);

        if(b == null){
            return null;
        }
        return BookingWithoutIdDto.builder()
                .depositPaid(booking.isDepositPaid())
                .finalPrice(b.getFinalPrice())
                .time(b.getTime())
                .tattooImage(b.getTattooImage())
                .date(b.getDate())
                .customer(b.getCustomer())
                .build();
    }

}
