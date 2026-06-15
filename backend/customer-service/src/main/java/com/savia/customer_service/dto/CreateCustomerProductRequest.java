package com.savia.customer_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateCustomerProductRequest(
        @NotBlank(message = "Product name is required")
        String productName,

        @NotBlank(message = "Brand is required")
        String brand,

        String model,

        @NotBlank(message = "Serial number is required")
        String serialNumber,

        LocalDate purchaseDate,

        LocalDate warrantyEndDate
) {
}