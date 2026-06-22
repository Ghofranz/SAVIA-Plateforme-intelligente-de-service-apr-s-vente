package com.savia.ai_service.dto;

import com.savia.ai_service.enums.KnowledgeArticleStatus;

import java.time.LocalDateTime;

public record KnowledgeArticleResponse(
        Long id,
        Long savCaseId,
        String caseReference,
        Long customerProductId,
        String sourceStatus,
        String originalProblem,
        String resolutionComment,
        String symptomSummary,
        String confirmedCause,
        String appliedSolution,
        String reusableKnowledge,
        String tags,
        KnowledgeArticleStatus status,
        boolean indexedInRag,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}