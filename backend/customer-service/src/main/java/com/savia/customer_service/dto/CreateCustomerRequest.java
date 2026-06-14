package com.savia.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
        @NotBlank(message = "Firstname is required")
        String firstname,

        @NotBlank(message = "Lastname is required")
        String lastname,

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        String phoneNumber,

        String address
) {
}