package com.savia.sav_service.dto;

import com.savia.sav_service.enums.SavCaseStatus;
import com.savia.sav_service.enums.SavPriority;

import java.time.LocalDateTime;

public record SavCaseResponse(
        Long id,
        String caseReference,
        Long customerId,
        Long customerProductId,
        Long createdByAuthUserId,
        Long assignedAgentAuthUserId,
        Long assignedTechnicianAuthUserId,
        String title,
        String description,
        SavCaseStatus status,
        SavPriority priority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt
) {
}