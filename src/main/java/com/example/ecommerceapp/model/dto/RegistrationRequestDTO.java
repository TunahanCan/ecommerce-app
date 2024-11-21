package com.example.ecommerceapp.model.dto;

public record RegistrationRequestDTO(
        String firstName,
        String lastName,
        String email,
        String password
) { }