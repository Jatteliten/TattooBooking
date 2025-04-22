package com.example.demo.services;

import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
import com.example.demo.repos.CustomerRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void deleteCustomer(Customer customer){
        List<Booking> bookingsToSetCustomerToNull = new ArrayList<>();
        List<Booking> bookingsToDelete = new ArrayList<>();

        for(Booking booking: customer.getBookings()){
            if(booking.getDate().isBefore(LocalDateTime.now())){
                booking.setCustomer(null);
                bookingsToSetCustomerToNull.add(booking);
            }else{
                bookingsToDelete.add(booking);
            }
        }

        if(!bookingsToSetCustomerToNull.isEmpty()){
            bookingService.saveListOfBookings(bookingsToSetCustomerToNull);
        }

        if(!bookingsToDelete.isEmpty()){
            bookingService.deleteBookings(bookingsToDelete);
        }

        customerRepo.delete(customer);
    }

    public void deleteAllCustomers(){
        customerRepo.deleteAll();
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
}
