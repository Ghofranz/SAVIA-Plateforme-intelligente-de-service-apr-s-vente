package com.savia.ai_service.service;

import com.savia.ai_service.entity.AiAnalysis;
import com.savia.ai_service.entity.KnowledgeArticle;
import com.savia.ai_service.enums.KnowledgeArticleStatus;
import com.savia.ai_service.event.SavCaseStatusUpdatedEvent;
import com.savia.ai_service.repository.AiAnalysisRepository;
import com.savia.ai_service.repository.KnowledgeArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeArticleService {

    private final KnowledgeArticleRagIndexer knowledgeArticleRagIndexer;
    private final KnowledgeArticleRepository knowledgeArticleRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final KnowledgeArticleGenerator knowledgeArticleGenerator;

    @Transactional
    public void handleSavCaseStatusUpdated(SavCaseStatusUpdatedEvent event) {
        if (!isKnowledgeStatus(event.newStatus())) {
            log.info(
                    "SAV case {} status {} does not require knowledge article generation.",
                    event.savCaseId(),
                    event.newStatus()
            );
            return;
        }

        if (knowledgeArticleRepository.existsBySavCaseId(event.savCaseId())) {
            log.info(
                    "Knowledge article already exists for SAV case {}.",
                    event.savCaseId()
            );
            return;
        }

        AiAnalysis analysis = aiAnalysisRepository.findBySavCaseId(event.savCaseId())
                .orElse(null);

        KnowledgeArticleGenerator.KnowledgeArticleInput input =
                buildInput(event, analysis);

        KnowledgeArticleGenerator.KnowledgeArticleResult result =
                knowledgeArticleGenerator.generate(input);

        KnowledgeArticle article = KnowledgeArticle.builder()
                .savCaseId(event.savCaseId())
                .caseReference(event.caseReference())
                .customerProductId(analysis == null ? null : analysis.getCustomerProductId())
                .sourceStatus(event.newStatus())
                .originalProblem(buildOriginalProblem(analysis))
                .resolutionComment(event.comment())
                .symptomSummary(result.symptomSummary())
                .confirmedCause(result.confirmedCause())
                .appliedSolution(result.appliedSolution())
                .reusableKnowledge(result.reusableKnowledge())
                .tags(result.tags())
                .status(KnowledgeArticleStatus.DRAFT)
                .indexedInRag(false)
                .build();

        KnowledgeArticle savedArticle = knowledgeArticleRepository.save(article);

        try {
            knowledgeArticleRagIndexer.indexArticle(savedArticle);
            savedArticle.setIndexedInRag(true);
            knowledgeArticleRepository.save(savedArticle);
        } catch (Exception exception) {
            log.warn(
                    "Knowledge article {} was saved but could not be indexed into RAG.",
                    savedArticle.getId(),
                    exception
            );
        }

        log.info(
                "Generated knowledge article for SAV case {} with status {}.",
                event.savCaseId(),
                event.newStatus()
        );
    }

    private boolean isKnowledgeStatus(String status) {
        if (status == null) {
            return false;
        }

        return status.equals("RESOLVED") || status.equals("CLOSED");
    }

    private KnowledgeArticleGenerator.KnowledgeArticleInput buildInput(
            SavCaseStatusUpdatedEvent event,
            AiAnalysis analysis
    ) {
        return new KnowledgeArticleGenerator.KnowledgeArticleInput(
                event.caseReference(),
                event.newStatus(),
                analysis == null ? null : analysis.getCustomerProductId(),
                analysis == null ? null : analysis.getOriginalTitle(),
                analysis == null ? null : analysis.getOriginalDescription(),
                analysis == null ? null : analysis.getDiagnosis(),
                analysis == null ? null : analysis.getPossibleCauses(),
                analysis == null ? null : analysis.getRecommendedActions(),
                event.comment()
        );
    }

    private String buildOriginalProblem(AiAnalysis analysis) {
        if (analysis == null) {
            return "Analyse IA initiale non disponible.";
        }

        return """
                Titre initial :
                %s

                Description initiale :
                %s

                Diagnostic IA initial :
                %s
                """.formatted(
                safe(analysis.getOriginalTitle()),
                safe(analysis.getOriginalDescription()),
                safe(analysis.getDiagnosis())
        );
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Non disponible" : value;
    }
}