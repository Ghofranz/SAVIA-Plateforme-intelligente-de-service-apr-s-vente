package com.savia.sav_service.dto;

import com.savia.sav_service.enums.SavPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSavCaseRequest(
        @NotNull(message = "Customer id is required")
        Long customerId,

        @NotNull(message = "Customer product id is required")
        Long customerProductId,

        @NotNull(message = "Creator auth user id is required")
        Long createdByAuthUserId,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        SavPriority priority
) {
}