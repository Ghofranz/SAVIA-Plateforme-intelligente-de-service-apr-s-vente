package com.savia.ai_service.dto;

public record AiDiagnosisResult(
        String diagnosis,
        String possibleCauses,
        String recommendedActions,
        String ragSources
) {
}