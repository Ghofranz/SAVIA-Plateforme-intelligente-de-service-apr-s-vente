package com.savia.sav_service.event;

import java.time.LocalDateTime;

public record SavCaseCreatedEvent(
        Long savCaseId,
        String caseReference,
        Long customerId,
        Long customerProductId,
        Long createdByAuthUserId,
        String title,
        String status,
        String priority,
        LocalDateTime createdAt
) {
}