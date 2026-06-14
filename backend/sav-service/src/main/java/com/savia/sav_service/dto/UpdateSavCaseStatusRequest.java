package com.savia.sav_service.dto;

import com.savia.sav_service.enums.SavCaseStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSavCaseStatusRequest(
        @NotNull(message = "New status is required")
        SavCaseStatus newStatus,

        String comment
) {
}