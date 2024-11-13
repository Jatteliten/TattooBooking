package com.example.demo.controller;

import com.example.demo.model.TattooImage;
import com.example.demo.services.TattooImageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class CustomerPageController {

    TattooImageService tattooImageService;

    public CustomerPageController(TattooImageService tattooImageService){
        this.tattooImageService = tattooImageService;
    }

    @RequestMapping("/")
    public String frontPage(Model model){
        model.addAttribute("images", tattooImageService.getAllFrontPageImagesUrls());
        return "index";
    }

    @RequestMapping("/about-me")
    public String aboutMe(){
        return "about-me";
    }

}
