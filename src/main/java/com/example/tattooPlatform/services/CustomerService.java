package com.example.tattooPlatform.services;

import com.example.tattooPlatform.model.Booking;
import com.example.tattooPlatform.model.Customer;
import com.example.tattooPlatform.repos.CustomerRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepo customerRepo;
    private final BookingService bookingService;

    public CustomerService(CustomerRepo customerRepo, BookingService bookingService) {
        this.customerRepo = customerRepo;
        this.bookingService = bookingService;
    }

    public void saveCustomer(Customer customer){
        customerRepo.save(customer);
    }

    @Transactional
    public String deleteCustomerAndChangeAssociatedBookings(Customer customer){
        String customerName = customer.getName();

        if(customer.getBookings() != null && !customer.getBookings().isEmpty()){
            bookingService.deleteFutureBookingsAndSetPastBookingsToNull(customer.getBookings());
            customer.setBookings(null);
        }

        customerRepo.delete(customer);

        return customerName;
    }

    public Customer getCustomerById(UUID id){
        return customerRepo.findById(id).orElse(null);
    }

    public List<Customer> getCustomerByNameContaining(String input){
        return customerRepo.findByNameContainingIgnoreCase(input)
                .stream().sorted(Comparator.comparing(Customer::getName)).toList();
    }

    public Customer getCustomerIfAtLeastOneContactMethodMatches(Customer customer) {
        Customer customerToFind;
        String phone = customer.getPhone();
        String instagram = customer.getInstagram();
        String email = customer.getEmail();
        if (phone != null && !phone.isEmpty()) {
            customerToFind = customerRepo.findByPhone(phone);
            if (customerToFind != null){
                return customerToFind;
            }
        }
        if (instagram != null && !instagram.isEmpty()) {
            customerToFind = customerRepo.findByInstagram(instagram);
            if (customerToFind != null){
                return customerToFind;
            }
        }
        if (email != null && !email.isEmpty()) {
            return customerRepo.findByEmail(customer.getEmail());
        }
        return null;
    }

    public Customer getCustomerByAnyField(String input) {
        return customerRepo.findByAnyContactMethod(input).orElse(null);
    }

    public List<Booking> getCustomersEligiblePreviousBookingsForTouchUp(Customer customer, Booking booking){
        List<Booking> eligibleBookings = new ArrayList<>(customer.getBookings());

        for(Booking customerBooking: customer.getBookings()){
            Booking previousBooking = customerBooking.getPreviousBooking();
            if(previousBooking != null){
                eligibleBookings.remove(previousBooking);
            }
            if(customerBooking.isTouchUp() || customerBooking.getDate().isAfter(booking.getDate())){
                eligibleBookings.remove(customerBooking);
            }
        }

        return eligibleBookings;
    }

    public List<Booking> sortCustomerBookings(Customer customer){
        return bookingService.sortBookingsByStartDateAndTime(customer.getBookings());
    }

    public int calculateCustomersTotalPaid(Customer customer){
        return customer.getBookings()
                .stream()
                .mapToInt(Booking::getFinalPrice)
                .sum();
    }

}
