package com.example.ecommerceapp.model.dto;

public record ProductRequestDTO(
        String name,
        String description,
        Double price
) {}