package com.savia.sav_service.dto;

import com.savia.sav_service.enums.SavCaseStatus;

import java.time.LocalDateTime;

public record SavCaseStatusHistoryResponse(
        Long id,
        Long savCaseId,
        SavCaseStatus oldStatus,
        SavCaseStatus newStatus,
        Long changedByAuthUserId,
        String comment,
        LocalDateTime changedAt
) {
}