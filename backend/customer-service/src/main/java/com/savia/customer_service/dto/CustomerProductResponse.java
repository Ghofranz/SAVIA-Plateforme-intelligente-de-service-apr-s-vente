package com.savia.customer_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerProductResponse(
        Long id,
        Long customerId,
        String productName,
        String brand,
        String model,
        String serialNumber,
        LocalDate purchaseDate,
        LocalDate warrantyEndDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}