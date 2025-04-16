package com.example.demo.controller.admin;

import com.example.demo.model.TattooImage;
import com.example.demo.services.CustomerService;
import com.example.demo.services.TattooImageService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('Admin')")
public class AdminController {

    private final TattooImageService tattooImageService;
    private final CustomerService customerService;

    public AdminController(TattooImageService tattooImageService, CustomerService customerService) {
        this.tattooImageService = tattooImageService;
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String loginSuccess(){
        return "admin/admin-landing-page";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            TattooImage image = tattooImageService.saveImage(file);
            model.addAttribute("message", "Image uploaded successfully: " + image.getName());
        } catch (IOException e) {
            model.addAttribute("message", "Failed to upload image");
        }
        model.addAttribute("images", tattooImageService.getAllImages());
        return "redirect:/admin/set-front-page-images";
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> viewImage(@PathVariable UUID id) throws MalformedURLException {
        TattooImage image = tattooImageService.getImage(id);
        Path imagePath = Paths.get(image.getUrl());
        Resource resource = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(resource);
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
