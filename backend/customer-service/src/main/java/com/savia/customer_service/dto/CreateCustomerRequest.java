package com.savia.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerRequest(
        @NotNull(message = "Auth user id is required")
        Long authUserId,

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