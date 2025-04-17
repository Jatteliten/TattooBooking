package com.example.demo.controller.admin;

import com.example.demo.services.CustomerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('Admin')")
public class AdminController {
    private final CustomerService customerService;

    public AdminController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String loginSuccess(){
        return "admin/admin-landing-page";
    }

    @RequestMapping("/customer")
    public String customerInformation(Model model){
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/customer";
    }

    @PostMapping("/delete-all-customers")
    public String deleteAllCustomers(){
        customerService.deleteAllCustomers();
        return "admin/customer";
    }

}
