package com.savia.ai_service.service;

import com.savia.ai_service.dto.AiAnalysisResponse;
import com.savia.ai_service.dto.AiDiagnosisResult;
import com.savia.ai_service.entity.AiAnalysis;
import com.savia.ai_service.enums.AiAnalysisStatus;
import com.savia.ai_service.event.SavCaseCreatedEvent;
import com.savia.ai_service.repository.AiAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final AiAnalysisRepository aiAnalysisRepository;
    private final AiDiagnosisGenerator aiDiagnosisGenerator;

    public void handleSavCaseCreated(SavCaseCreatedEvent event) {
        if (aiAnalysisRepository.existsBySavCaseId(event.savCaseId())) {
            return;
        }

        AiAnalysis analysis = AiAnalysis.builder()
                .savCaseId(event.savCaseId())
                .caseReference(event.caseReference())
                .customerId(event.customerId())
                .customerProductId(event.customerProductId())
                .status(AiAnalysisStatus.PENDING)
                .build();

        analysis = aiAnalysisRepository.save(analysis);

        try {
            analysis.setStatus(AiAnalysisStatus.PROCESSING);
            analysis = aiAnalysisRepository.save(analysis);

            AiDiagnosisResult result = aiDiagnosisGenerator.generateDiagnosis(event);

            analysis.setDiagnosis(result.diagnosis());
            analysis.setPossibleCauses(result.possibleCauses());
            analysis.setRecommendedActions(result.recommendedActions());
            analysis.setRagSources(result.ragSources());
            analysis.setStatus(AiAnalysisStatus.COMPLETED);
            analysis.setErrorMessage(null);

            aiAnalysisRepository.save(analysis);
        } catch (Exception exception) {
            analysis.setStatus(AiAnalysisStatus.FAILED);
            analysis.setErrorMessage(exception.getMessage());

            aiAnalysisRepository.save(analysis);
        }
    }

    @Transactional(readOnly = true)
    public AiAnalysisResponse getAnalysisBySavCaseId(Long savCaseId) {
        AiAnalysis analysis = aiAnalysisRepository.findBySavCaseId(savCaseId)
                .orElseThrow(() -> new IllegalArgumentException("AI analysis not found for SAV case id: " + savCaseId));

        return mapToResponse(analysis);
    }

    private AiAnalysisResponse mapToResponse(AiAnalysis analysis) {
        return new AiAnalysisResponse(
                analysis.getId(),
                analysis.getSavCaseId(),
                analysis.getCaseReference(),
                analysis.getCustomerId(),
                analysis.getCustomerProductId(),
                analysis.getStatus(),
                analysis.getDiagnosis(),
                analysis.getPossibleCauses(),
                analysis.getRecommendedActions(),
                analysis.getRagSources(),
                analysis.getErrorMessage(),
                analysis.getCreatedAt(),
                analysis.getUpdatedAt()
        );
    }
}