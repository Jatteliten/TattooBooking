package com.example.tattooplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TattooPlatform {

    public static void main(String[] args) {
        SpringApplication.run(TattooPlatform.class, args);
    }

}
