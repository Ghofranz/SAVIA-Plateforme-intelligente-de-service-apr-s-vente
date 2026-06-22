package com.savia.ai_service.service;

import com.savia.ai_service.entity.KnowledgeArticle;
import com.savia.ai_service.enums.KnowledgeArticleStatus;
import com.savia.ai_service.repository.KnowledgeArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeArticleRagIndexer {

    private final VectorStore vectorStore;
    private final KnowledgeArticleRepository knowledgeArticleRepository;

    public void indexArticle(KnowledgeArticle article) {
        if (article == null || article.getId() == null) {
            return;
        }

        if (article.getStatus() != KnowledgeArticleStatus.VALIDATED) {
            log.info(
                    "Knowledge article {} has status {}. It will not be indexed until validation.",
                    article.getId(),
                    article.getStatus()
            );
            return;
        }

        String text = buildRagText(article);

        if (text.isBlank()) {
            log.warn("Knowledge article {} has empty RAG text. Skipping indexing.", article.getId());
            return;
        }

        Document document = Document.builder()
                .text(text)
                .metadata("source", "knowledge_article_" + article.getId())
                .metadata("type", "resolved_case_knowledge")
                .metadata("caseReference", article.getCaseReference())
                .metadata("savCaseId", String.valueOf(article.getSavCaseId()))
                .metadata("customerProductId", article.getCustomerProductId() == null ? "unknown" : String.valueOf(article.getCustomerProductId()))
                .metadata("sourceStatus", article.getSourceStatus())
                .metadata("knowledgeStatus", article.getStatus().name())
                .metadata("tags", article.getTags() == null ? "" : article.getTags())
                .build();

        vectorStore.add(List.of(document));

        log.info(
                "Indexed VALIDATED knowledge article {} into RAG vector store for SAV case {}.",
                article.getId(),
                article.getSavCaseId()
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    public void indexValidatedKnowledgeArticlesOnStartup() {
        List<KnowledgeArticle> articles = knowledgeArticleRepository.findAll();

        if (articles.isEmpty()) {
            log.info("No existing knowledge articles found for RAG startup indexing.");
            return;
        }

        int indexedCount = 0;

        for (KnowledgeArticle article : articles) {
            if (article.getStatus() != KnowledgeArticleStatus.VALIDATED) {
                continue;
            }

            indexArticle(article);
            indexedCount++;
        }

        log.info("Indexed {} validated knowledge articles into RAG vector store on startup.", indexedCount);
    }

    private String buildRagText(KnowledgeArticle article) {
        return """
                Type de source : fiche de connaissance validée issue d'un dossier SAV résolu
                Référence dossier : %s
                ID dossier SAV : %s
                ID produit client : %s
                Statut final du dossier : %s
                Statut de la fiche : %s
                Tags : %s

                Problème initial :
                %s

                Commentaire de résolution agent :
                %s

                Résumé du symptôme :
                %s

                Cause confirmée ou probable :
                %s

                Solution appliquée :
                %s

                Connaissance réutilisable :
                %s
                """.formatted(
                safe(article.getCaseReference()),
                article.getSavCaseId(),
                article.getCustomerProductId() == null ? "Non disponible" : article.getCustomerProductId(),
                safe(article.getSourceStatus()),
                article.getStatus() == null ? "Non disponible" : article.getStatus().name(),
                safe(article.getTags()),
                safe(article.getOriginalProblem()),
                safe(article.getResolutionComment()),
                safe(article.getSymptomSummary()),
                safe(article.getConfirmedCause()),
                safe(article.getAppliedSolution()),
                safe(article.getReusableKnowledge())
        );
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Non disponible" : value;
    }
}