package com.example.ecommerceapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EcommerceAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceAppApplication.class, args);
    }
}
