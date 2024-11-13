package com.example.demo.controller;

import com.example.demo.services.TattooImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


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
