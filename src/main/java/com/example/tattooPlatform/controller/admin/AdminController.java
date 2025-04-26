package com.example.tattooPlatform.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('Admin')")
public class AdminController {

    public AdminController() {}

    @GetMapping("/")
    public String loginSuccess(){
        return "admin/admin-landing-page";
    }

}
