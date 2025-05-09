package com.example.tattooplatform.controller.admin;


import com.example.tattooplatform.controller.ModelFeedback;
import com.example.tattooplatform.model.Customer;
import com.example.tattooplatform.services.CustomerService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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
    private static final String CUSTOMER = "customer";
    private static final String CUSTOMER_TEMPLATE = "admin/customer";

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String viewCustomers(){
        return CUSTOMER_TEMPLATE;
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
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "No customer with \"" + searchInput + "\" in their name found.");
        }

        return CUSTOMER_TEMPLATE;
    }

    @GetMapping("/edit-customer")
    public String editCustomerInformation(@RequestParam UUID id, Model model){
        model.addAttribute(CUSTOMER, customerService.getCustomerById(id));

        return "admin/edit-customer-information";
    }

    @PostMapping("/save-customer-changes")
    public String saveCustomerChanges(@RequestParam UUID id,
                                      @RequestParam String email,
                                      @RequestParam String phone,
                                      @RequestParam String instagram, Model model){
        Customer customerToChange = customerService.getCustomerById(id);

        try {
            customerToChange.setEmail(email.isBlank() ? null : email);
            customerToChange.setPhone(phone.isBlank() ? null : phone);
            customerToChange.setInstagram(instagram.isBlank() ? null : instagram);
            customerService.saveCustomer(customerToChange);
            model.addAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(), "Customer information changed.");
            return populateModelIfCustomerExists(customerToChange, "Changes could not be applied.", model);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "Email, phone, or Instagram already exists for another customer.");
            model.addAttribute(CUSTOMER, customerService.getCustomerById(id));
            return "admin/edit-customer-information";
        }

    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(@RequestParam UUID customerId, Model model){
        String customerName = customerService.deleteCustomerAndChangeAssociatedBookings(
                customerService.getCustomerById(customerId));

        model.addAttribute("deletedCustomer", customerName + " removed.");
        return CUSTOMER_TEMPLATE;
    }

    public String populateModelIfCustomerExists(Customer customer, String errorMessage, Model model){
        if(customer != null){
            if(customer.getBookings() != null){
                customer.setBookings(customerService.sortCustomerBookings(customer).reversed());
            }
            model.addAttribute(CUSTOMER, customer);
            model.addAttribute("totalPaid", customerService.calculateCustomersTotalPaid(customer));
        }else{
            model.addAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(), errorMessage);
        }

        return CUSTOMER_TEMPLATE;
    }

}
