package com.savia.ai_service.dto;

import com.savia.ai_service.enums.AiAnalysisStatus;

import java.time.LocalDateTime;

public record AiAnalysisResponse(
        Long id,
        Long savCaseId,
        String caseReference,
        Long customerId,
        Long customerProductId,
        AiAnalysisStatus status,
        String diagnosis,
        String possibleCauses,
        String recommendedActions,
        String ragSources,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}