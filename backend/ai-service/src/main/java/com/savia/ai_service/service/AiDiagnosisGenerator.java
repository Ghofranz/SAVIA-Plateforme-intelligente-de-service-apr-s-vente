package com.savia.ai_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.savia.ai_service.dto.AiDiagnosisResult;
import com.savia.ai_service.event.SavCaseCreatedEvent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiDiagnosisGenerator {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiDiagnosisGenerator(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public AiDiagnosisResult generateDiagnosis(SavCaseCreatedEvent event) {
        String prompt = """
                Analyse le dossier SAV suivant.

                Données du dossier :
                - Référence : %s
                - ID dossier SAV : %d
                - ID client : %d
                - ID produit client : %d
                - Titre : %s
                - Description client : %s
                - Priorité déclarée : %s

                Réponds uniquement avec un JSON valide.
                N'ajoute aucun texte avant ou après le JSON.
                N'utilise pas de Markdown.

                Format obligatoire :

                {
                  "diagnosis": "diagnostic probable clair et court",
                  "possibleCauses": "liste des causes possibles sous forme de phrases courtes",
                  "recommendedActions": "liste des actions recommandées pour l'agent SAV"
                }
                """.formatted(
                event.caseReference(),
                event.savCaseId(),
                event.customerId(),
                event.customerProductId(),
                event.title(),
                event.description(),
                event.priority() == null ? "Non spécifiée" : event.priority()
        );

        String response = chatClient.prompt()
                .system("""
                        Tu es un assistant expert en service après-vente.
                        Tu dois produire une analyse SAV claire, professionnelle et exploitable.
                        Tu dois impérativement répondre avec un JSON valide uniquement.
                        """)
                .user(prompt)
                .call()
                .content();

        return parseResponse(response);
    }

    private AiDiagnosisResult parseResponse(String response) {
        if (response == null || response.isBlank()) {
            return new AiDiagnosisResult(
                    "Aucun diagnostic généré.",
                    "Aucune cause possible générée.",
                    "Aucune action recommandée générée.",
                    "Mode Ollama simple sans RAG. Les sources documentaires seront ajoutées dans le bloc RAG."
            );
        }

        try {
            String cleanedResponse = cleanJsonResponse(response);
            JsonNode root = objectMapper.readTree(cleanedResponse);

            return new AiDiagnosisResult(
                    root.path("diagnosis").asText("Diagnostic non disponible."),
                    root.path("possibleCauses").asText("Causes possibles non disponibles."),
                    root.path("recommendedActions").asText("Actions recommandées non disponibles."),
                    "Mode Ollama simple sans RAG. Les sources documentaires seront ajoutées dans le bloc RAG."
            );
        } catch (Exception exception) {
            return new AiDiagnosisResult(
                    response.trim(),
                    "Impossible d'extraire automatiquement les causes possibles.",
                    "Impossible d'extraire automatiquement les actions recommandées.",
                    "Mode Ollama simple sans RAG. Réponse brute sauvegardée suite à une erreur de parsing JSON."
            );
        }
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