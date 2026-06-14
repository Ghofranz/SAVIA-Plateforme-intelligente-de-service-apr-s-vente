package com.savia.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCustomerProductRequest(
        @NotNull(message = "Customer id is required")
        Long customerId,

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