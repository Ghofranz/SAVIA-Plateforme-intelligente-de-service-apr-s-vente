package com.savia.ai_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KnowledgeArticleGenerator {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public KnowledgeArticleGenerator(
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper
    ) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public KnowledgeArticleResult generate(KnowledgeArticleInput input) {
        String prompt = """
                Génère une fiche de connaissance SAV réutilisable à partir d'un dossier résolu.

                Données du dossier :
                - Référence : %s
                - Statut final : %s
                - Produit client ID : %s

                Problème initial :
                - Titre : %s
                - Description : %s

                Analyse IA initiale :
                - Diagnostic : %s
                - Causes possibles : %s
                - Actions recommandées : %s

                Commentaire de résolution de l'agent :
                %s

                Objectif :
                Créer une fiche courte, claire et réutilisable pour aider les prochains diagnostics similaires.

                Règles :
                - Ne pas inventer une cause confirmée si le commentaire agent ne la confirme pas.
                - Si la cause n'est pas certaine, écrire "Cause probable à confirmer".
                - La fiche doit être exploitable par un agent SAV.
                - Le contenu doit pouvoir être indexé plus tard dans un RAG.

                Réponds uniquement avec un JSON valide.
                N'ajoute aucun texte avant ou après le JSON.
                N'utilise pas de Markdown.

                Format obligatoire :
                {
                  "symptomSummary": "résumé court du symptôme",
                  "confirmedCause": "cause confirmée ou cause probable à confirmer",
                  "appliedSolution": "solution appliquée ou recommandée",
                  "reusableKnowledge": "connaissance réutilisable pour des cas similaires",
                  "tags": "mots-clés séparés par des virgules"
                }
                """.formatted(
                input.caseReference(),
                input.finalStatus(),
                input.customerProductId(),
                safe(input.originalTitle()),
                safe(input.originalDescription()),
                safe(input.aiDiagnosis()),
                safe(input.aiPossibleCauses()),
                safe(input.aiRecommendedActions()),
                safe(input.resolutionComment())
        );

        String response = chatClient.prompt()
                .system("""
                        Tu es un expert SAV chargé de capitaliser les dossiers résolus.
                        Tu transformes les résolutions humaines en fiches de connaissance fiables.
                        Tu dois répondre uniquement avec un JSON valide.
                        """)
                .user(prompt)
                .call()
                .content();

        return parseResponse(response);
    }

    private KnowledgeArticleResult parseResponse(String response) {
        if (response == null || response.isBlank()) {
            return fallbackResult();
        }

        try {
            String cleaned = cleanJsonResponse(response);
            JsonNode root = objectMapper.readTree(cleaned);

            return new KnowledgeArticleResult(
                    getText(root, "symptomSummary", "symptom_summary", "symptome"),
                    getText(root, "confirmedCause", "confirmed_cause", "cause"),
                    getText(root, "appliedSolution", "applied_solution", "solution"),
                    getText(root, "reusableKnowledge", "reusable_knowledge", "knowledge"),
                    getText(root, "tags", "keywords")
            );
        } catch (Exception exception) {
            log.warn("Failed to parse knowledge article JSON. Using fallback content.", exception);
            return fallbackResult();
        }
    }

    private KnowledgeArticleResult fallbackResult() {
        return new KnowledgeArticleResult(
                "Symptôme à analyser à partir du dossier SAV.",
                "Cause probable à confirmer.",
                "Solution appliquée à compléter par l'agent SAV.",
                "Fiche générée automatiquement à partir d'un dossier résolu. Validation humaine recommandée.",
                "sav, resolution, diagnostic"
        );
    }

    private String getText(JsonNode root, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode node = root.path(fieldName);

            if (!node.isMissingNode() && !node.isNull()) {
                if (node.isTextual()) {
                    return node.asText();
                }

                return node.toString();
            }
        }

        return "";
    }

    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }

        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Non disponible" : value;
    }

    public record KnowledgeArticleInput(
            String caseReference,
            String finalStatus,
            Long customerProductId,
            String originalTitle,
            String originalDescription,
            String aiDiagnosis,
            String aiPossibleCauses,
            String aiRecommendedActions,
            String resolutionComment
    ) {
    }

    public record KnowledgeArticleResult(
            String symptomSummary,
            String confirmedCause,
            String appliedSolution,
            String reusableKnowledge,
            String tags
    ) {
    }
}