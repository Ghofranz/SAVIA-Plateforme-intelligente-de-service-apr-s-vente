package com.savia.ai_service.event;

import java.time.LocalDateTime;

public record SavCaseStatusUpdatedEvent(
        Long savCaseId,
        String caseReference,
        String oldStatus,
        String newStatus,
        Long changedByAuthUserId,
        String comment,
        LocalDateTime changedAt
) {
}