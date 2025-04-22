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

    @GetMapping("/search-customer")
    public String searchCustomer(@RequestParam String searchInput, Model model){
        Customer customer = customerService.findCustomerByAnyField(searchInput);

        if(customer != null){
            model.addAttribute("customer", customer);
        }else{
            model.addAttribute("customerFindError",
                    "Can't find customer with information \"" + searchInput + "\"");
        }

        return "admin/customer";
    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(UUID customerId, Model model){
        String customerName = customerService.deleteCustomerAndChangeAssociatedBookingsById(customerId);

        model.addAttribute("deletedCustomer", customerName + " removed.");
        return "admin/customer";
    }

}
