package com.savia.customer_service.dto;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        Long authUserId,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}