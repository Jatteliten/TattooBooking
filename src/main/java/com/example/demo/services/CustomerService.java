package com.example.demo.services;

import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.repos.CustomerRepo;
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
        List<Booking> touchedUpBookings = new ArrayList<>();
        List<Booking> customersBookings = customer.getBookings();

        for(Booking customerBooking: customersBookings){
            if(customerBooking.getPreviousBooking() != null){
                touchedUpBookings.add(customerBooking.getPreviousBooking());
            }
        }

        return customersBookings.stream()
                .filter(b ->
                        !b.isTouchUp() && b.getDate().isBefore(booking.getDate()) && !touchedUpBookings.contains(b))
                .toList();
    }

    public List<Booking> sortCustomerBookings(Customer customer){
        return bookingService.sortBookingsByStartDateAndTime(customer.getBookings());
    }

}
