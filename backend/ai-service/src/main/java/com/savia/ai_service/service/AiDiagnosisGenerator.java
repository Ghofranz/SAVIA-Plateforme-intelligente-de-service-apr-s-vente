package com.savia.ai_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.savia.ai_service.dto.AiDiagnosisResult;
import com.savia.ai_service.event.SavCaseCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AiDiagnosisGenerator {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final RagRetrievalService ragRetrievalService;

    public AiDiagnosisGenerator(
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper,
            RagRetrievalService ragRetrievalService
    ) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
        this.ragRetrievalService = ragRetrievalService;
    }

    public AiDiagnosisResult generateDiagnosis(SavCaseCreatedEvent event) {
        String ragQuery = """
                Titre du problème: %s
                Description client: %s
                Priorité: %s
                ID produit client: %d
                """.formatted(
                event.title(),
                event.description(),
                event.priority() == null ? "Non spécifiée" : event.priority(),
                event.customerProductId()
        );

        RagRetrievalService.RagContext ragContext = ragRetrievalService.retrieveContext(ragQuery);

        String prompt = """
                Analyse le dossier SAV suivant en utilisant prioritairement le contexte documentaire SAV fourni.

                Données du dossier :
                - Référence : %s
                - ID dossier SAV : %d
                - ID client : %d
                - ID produit client : %d
                - Titre : %s
                - Description client : %s
                - Priorité déclarée : %s

                Contexte documentaire SAV pertinent :
                %s

                Règles obligatoires :
                - Utilise le contexte documentaire SAV quand il est pertinent.
                - Si le contexte documentaire ne suffit pas, complète avec un raisonnement technique prudent.
                - Ne prétends jamais qu'une cause est certaine sans preuve.
                - Ne décide pas seul d'un remboursement, d'un remplacement ou d'un rejet.
                - Propose des actions concrètes exploitables par un agent SAV.
                - Mentionne la vérification de garantie si elle est pertinente.
                - La décision finale reste humaine et appartient à l'agent SAV.

                Réponds uniquement avec un JSON valide.
                N'ajoute aucun texte avant ou après le JSON.
                N'utilise pas de Markdown.

                Le JSON doit contenir exactement ces trois clés :
                {
                  "diagnosis": "diagnostic probable clair et court",
                  "possibleCauses": "causes possibles sous forme de phrases courtes",
                  "recommendedActions": "actions recommandées concrètes pour l'agent SAV"
                }

                Important :
                - Les valeurs doivent être du texte simple.
                - N'utilise pas de tableau.
                - N'utilise pas d'objet imbriqué.
                """.formatted(
                event.caseReference(),
                event.savCaseId(),
                event.customerId(),
                event.customerProductId(),
                event.title(),
                event.description(),
                event.priority() == null ? "Non spécifiée" : event.priority(),
                ragContext.context()
        );

        String response = chatClient.prompt()
                .system("""
                        Tu es un assistant expert en service après-vente.
                        Tu aides les agents SAV à diagnostiquer les dossiers clients.
                        Tu dois utiliser les documents SAV fournis comme contexte prioritaire.
                        Tu dois répondre avec prudence et ne jamais inventer une certitude technique.
                        Tu dois impérativement répondre avec un JSON valide uniquement.
                        """)
                .user(prompt)
                .call()
                .content();

        log.info("Raw AI response for SAV case {}: {}", event.savCaseId(), response);

        return parseResponse(response, ragContext.sources());
    }

    private AiDiagnosisResult parseResponse(String response, String ragSources) {
        if (response == null || response.isBlank()) {
            return new AiDiagnosisResult(
                    "Aucun diagnostic généré.",
                    "Aucune cause possible générée.",
                    "Aucune action recommandée générée.",
                    ragSources
            );
        }

        try {
            String cleanedResponse = cleanJsonResponse(response);
            JsonNode root = objectMapper.readTree(cleanedResponse);

            String diagnosis = extractText(
                    root,
                    "diagnosis",
                    "diagnostic",
                    "diagnosticProbable",
                    "probableDiagnosis"
            );

            String possibleCauses = extractText(
                    root,
                    "possibleCauses",
                    "possible_causes",
                    "causes",
                    "causesPossibles",
                    "probableCauses"
            );

            String recommendedActions = extractText(
                    root,
                    "recommendedActions",
                    "recommended_actions",
                    "actions",
                    "actionsRecommandees",
                    "recommendations"
            );

            return new AiDiagnosisResult(
                    fallback(diagnosis, "Diagnostic non disponible."),
                    fallback(possibleCauses, "Causes possibles non disponibles."),
                    fallback(recommendedActions, "Actions recommandées non disponibles."),
                    ragSources
            );
        } catch (Exception exception) {
            log.warn("Failed to parse AI JSON response. Saving raw response instead.", exception);

            return new AiDiagnosisResult(
                    response.trim(),
                    "Impossible d'extraire automatiquement les causes possibles.",
                    "Impossible d'extraire automatiquement les actions recommandées.",
                    ragSources
            );
        }
    }

    private String extractText(JsonNode root, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode node = root.path(fieldName);

            if (node.isMissingNode() || node.isNull()) {
                continue;
            }

            if (node.isTextual()) {
                return node.asText();
            }

            if (node.isArray()) {
                List<String> values = new ArrayList<>();

                for (JsonNode item : node) {
                    if (item.isTextual()) {
                        values.add("- " + item.asText());
                    } else {
                        values.add("- " + item.toString());
                    }
                }

                return String.join("\n", values);
            }

            if (node.isObject()) {
                return node.toPrettyString();
            }

            return node.asText();
        }

        return "";
    }

    private String fallback(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
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
}