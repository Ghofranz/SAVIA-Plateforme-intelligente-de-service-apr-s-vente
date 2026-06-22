package com.savia.ai_service.service;

import com.savia.ai_service.dto.KnowledgeArticleResponse;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeArticleService {

    private final KnowledgeArticleRepository knowledgeArticleRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final KnowledgeArticleGenerator knowledgeArticleGenerator;
    private final KnowledgeArticleRagIndexer knowledgeArticleRagIndexer;

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

        log.info(
                "Generated DRAFT knowledge article {} for SAV case {}. Waiting for human validation before RAG indexing.",
                savedArticle.getId(),
                event.savCaseId()
        );
    }

    @Transactional(readOnly = true)
    public List<KnowledgeArticleResponse> getAllArticles() {
        return knowledgeArticleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public KnowledgeArticleResponse getArticleById(Long id) {
        KnowledgeArticle article = knowledgeArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge article not found with id: " + id));

        return mapToResponse(article);
    }

    @Transactional
    public KnowledgeArticleResponse validateArticle(Long id) {
        KnowledgeArticle article = knowledgeArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge article not found with id: " + id));

        if (article.getStatus() == KnowledgeArticleStatus.ARCHIVED) {
            throw new IllegalStateException("Archived knowledge articles cannot be validated.");
        }

        article.setStatus(KnowledgeArticleStatus.VALIDATED);

        KnowledgeArticle savedArticle = knowledgeArticleRepository.save(article);

        if (!savedArticle.isIndexedInRag()) {
            knowledgeArticleRagIndexer.indexArticle(savedArticle);
            savedArticle.setIndexedInRag(true);
            savedArticle = knowledgeArticleRepository.save(savedArticle);
        }

        log.info(
                "Knowledge article {} validated and indexed into RAG.",
                savedArticle.getId()
        );

        return mapToResponse(savedArticle);
    }

    @Transactional
    public KnowledgeArticleResponse archiveArticle(Long id) {
        KnowledgeArticle article = knowledgeArticleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge article not found with id: " + id));

        if (article.isIndexedInRag()) {
            throw new IllegalStateException(
                    "This article is already indexed in the current in-memory RAG. Restart ai-service after archiving to remove it from the active vector store."
            );
        }

        article.setStatus(KnowledgeArticleStatus.ARCHIVED);
        article.setIndexedInRag(false);

        KnowledgeArticle savedArticle = knowledgeArticleRepository.save(article);

        log.info("Knowledge article {} archived.", savedArticle.getId());

        return mapToResponse(savedArticle);
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

    private KnowledgeArticleResponse mapToResponse(KnowledgeArticle article) {
        return new KnowledgeArticleResponse(
                article.getId(),
                article.getSavCaseId(),
                article.getCaseReference(),
                article.getCustomerProductId(),
                article.getSourceStatus(),
                article.getOriginalProblem(),
                article.getResolutionComment(),
                article.getSymptomSummary(),
                article.getConfirmedCause(),
                article.getAppliedSolution(),
                article.getReusableKnowledge(),
                article.getTags(),
                article.getStatus(),
                article.isIndexedInRag(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Non disponible" : value;
    }
}