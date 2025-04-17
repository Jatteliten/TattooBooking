package com.example.demo.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/images")
@PreAuthorize("hasAuthority('Admin')")
public class ImageController {

}
