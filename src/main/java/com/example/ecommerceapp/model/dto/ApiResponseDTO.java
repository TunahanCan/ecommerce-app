package com.example.ecommerceapp.model.dto;


public record ApiResponseDTO<T>(
        boolean success,
        String message,
        T data
) {

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseDTO<T> build() {
            return new ApiResponseDTO<>(success, message, data);
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}
