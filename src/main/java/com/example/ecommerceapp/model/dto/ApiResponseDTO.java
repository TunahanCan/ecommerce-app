package com.example.ecommerceapp.model.dto;

import lombok.Builder;

@Builder
public record ApiResponseDTO<T>(
            boolean success,
            String message,
            T data
) { }
