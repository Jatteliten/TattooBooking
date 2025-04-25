package com.example.demo.controller.admin;


import com.example.demo.model.Customer;
import com.example.demo.services.CustomerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasAuthority('Admin')")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String viewCustomers(){
        return "admin/customer";
    }

    @GetMapping("/customer")
    public String getCustomerById(@RequestParam UUID id, Model model){
        return populateModelIfCustomerExists(
                customerService.getCustomerById(id),
                "Can't find customer with ID \"" + id + "\"", model);
    }

    @GetMapping("/search-customer")
    public String searchCustomerByContactInformation(@RequestParam String searchInput, Model model){
        return populateModelIfCustomerExists(
                customerService.getCustomerByAnyField(searchInput),
                "Can't find customer with information \"" + searchInput + "\"", model);
    }

    @GetMapping("/search-customer-name")
    public String searchCustomersByName(@RequestParam String searchInput, Model model){
        List<Customer> customerList = customerService.getCustomerByNameContaining(searchInput);
        if(!customerList.isEmpty()){
            model.addAttribute("customerList", customerList);
        }else{
            model.addAttribute("customerFindError",
                    "No customer with \"" + searchInput + "\" in their name found.");
        }

        return "admin/customer";
    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(@RequestParam UUID customerId, Model model){
        String customerName = customerService.deleteCustomerAndChangeAssociatedBookings(
                customerService.getCustomerById(customerId));

        model.addAttribute("deletedCustomer", customerName + " removed.");
        return "admin/customer";
    }

    public String populateModelIfCustomerExists(Customer customer, String errorMessage, Model model){
        if(customer != null){
            customer.setBookings(customerService.sortCustomerBookings(customer));
            model.addAttribute("customer", customer);
        }else{
            model.addAttribute("customerFindError", errorMessage);
        }

        return "admin/customer";
    }

}
