package com.savia.ai_service.event;

import java.time.LocalDateTime;

public record SavCaseCreatedEvent(
        Long savCaseId,
        String caseReference,
        Long customerId,
        Long customerProductId,
        Long createdByAuthUserId,
        String title,
        String description,
        String priority,
        LocalDateTime createdAt
) {
}