package com.example.demo.services;

import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.repos.CustomerRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public List<Customer> getAllCustomers(){
        return customerRepo.findAll();
    }

    public void saveCustomer(Customer customer){
        customerRepo.save(customer);
    }

    public List<Customer> findCustomerByNameContaining(String input){
        return customerRepo.findByNameContainingIgnoreCase(input)
                .stream().sorted(Comparator.comparing(Customer::getName)).toList();
    }

    @Transactional
    public String deleteCustomerAndChangeAssociatedBookingsById(UUID id){
        Customer customer = findCustomerById(id);
        String customerName = customer.getName();

        if(customer.getBookings() != null && !customer.getBookings().isEmpty()){
            bookingService.deleteFutureBookingsAndSetPastBookingsCustomerToNull(customer.getBookings());
        }

        customerRepo.delete(customer);

        return customerName;
    }

    public Customer findCustomerById(UUID id){
        return customerRepo.findById(id).orElse(null);
    }

    public Customer findCustomerIfAtLeastOneContactMethodMatches(Customer customer) {
        Customer customerToFind;
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            customerToFind = findCustomerByAnyField(customer.getPhone());
            if (customerToFind != null){
                return customerToFind;
            }
        }
        if (customer.getInstagram() != null && !customer.getInstagram().isEmpty()) {
            customerToFind = findCustomerByAnyField(customer.getInstagram());
            if (customerToFind != null){
                return customerToFind;
            }
        }
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            return findCustomerByAnyField(customer.getEmail());
        }
        return null;
    }

    public Customer findCustomerByAnyField(String input) {
        return customerRepo.findByAnyContactMethod(input).orElse(null);
    }

    public List<Booking> sortCustomerBookings(Customer customer){
        return bookingService.sortBookingsByStartDateAndTime(customer.getBookings());
    }


}
