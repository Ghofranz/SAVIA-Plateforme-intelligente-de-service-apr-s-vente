package com.savia.ai_service.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class PromptSecurityService {

    private static final String REMOVED_INSTRUCTION = "[instruction utilisateur supprimée pour sécurité]";

    private final List<String> dangerousExpressions = List.of(
            "ignore les instructions",
            "ignore les regles",
            "ignore toutes les instructions",
            "ignore les instructions precedentes",
            "ignore all previous instructions",
            "forget previous instructions",
            "oublie les instructions",
            "oublie les regles",
            "revele le prompt",
            "revele le prompt systeme",
            "revele le message systeme",
            "revele les instructions",
            "montre le prompt",
            "donne le prompt",
            "show the system prompt",
            "show me the system prompt",
            "developer message",
            "system prompt",
            "jailbreak",
            "bypass security",
            "bypass rules",
            "bypass instructions",
            "desactive la securite",
            "desactiver la securite",
            "disable security",
            "tu n es plus un assistant",
            "tu n'es plus un assistant",
            "act as",
            "do not follow the instructions",
            "ne suis pas les instructions",
            "contourne les regles",
            "contourne la securite"
    );

    public SecuredInput secure(String title, String description) {
        boolean titleSuspicious = isSuspicious(title);
        boolean descriptionSuspicious = isSuspicious(description);

        boolean suspicious = titleSuspicious || descriptionSuspicious;

        String safeTitle = suspicious
                ? sanitizeBySentence(title)
                : safeText(title);

        String safeDescription = suspicious
                ? sanitizeBySentence(description)
                : safeText(description);

        String warning = suspicious
                ? "Entrée utilisateur suspecte détectée. Les instructions non liées au problème SAV ont été neutralisées."
                : "Aucune tentative d'injection détectée.";

        return new SecuredInput(
                safeText(title),
                safeText(description),
                safeTitle,
                safeDescription,
                suspicious,
                warning
        );
    }

    public boolean isSuspicious(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }

        String normalized = normalize(input);

        return dangerousExpressions.stream()
                .anyMatch(normalized::contains);
    }

    private String sanitizeBySentence(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String[] sentences = input.split("(?<=[.!?])\\s+");
        StringBuilder sanitized = new StringBuilder();

        for (String sentence : sentences) {
            if (isSuspicious(sentence)) {
                sanitized.append(REMOVED_INSTRUCTION).append(" ");
            } else {
                sanitized.append(sentence.trim()).append(" ");
            }
        }

        return sanitized.toString().trim();
    }

    private String safeText(String input) {
        return input == null ? "" : input.trim();
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        normalized = normalized
                .replace("’", "'")
                .replace("`", "'")
                .replace("´", "'")
                .replaceAll("[^a-zA-Z0-9' ]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();

        return normalized;
    }

    public record SecuredInput(
            String originalTitle,
            String originalDescription,
            String safeTitle,
            String safeDescription,
            boolean suspicious,
            String warning
    ) {
    }
}