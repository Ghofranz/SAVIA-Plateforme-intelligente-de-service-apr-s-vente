package com.savia.sav_service.dto;

import com.savia.sav_service.enums.SavCaseStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSavCaseStatusRequest(
        @NotNull(message = "New status is required")
        SavCaseStatus newStatus,

        @NotNull(message = "Changed by auth user id is required")
        Long changedByAuthUserId,

        String comment
) {
}