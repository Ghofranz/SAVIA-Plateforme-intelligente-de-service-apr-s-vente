package com.savia.ai_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagRetrievalService {

    private final VectorStore vectorStore;

    public RagContext retrieveContext(String query) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .similarityThreshold(0.55)
                        .build()
        );

        if (documents == null || documents.isEmpty()) {
            return new RagContext(
                    "Aucun document SAV pertinent trouvé.",
                    "Aucune source RAG utilisée."
            );
        }

        String context = documents.stream()
                .map(this::formatDocumentContent)
                .collect(Collectors.joining("\n\n---\n\n"));

        String sources = documents.stream()
                .map(this::formatSource)
                .distinct()
                .collect(Collectors.joining(", "));

        return new RagContext(context, sources);
    }

    private String formatDocumentContent(Document document) {
        String source = String.valueOf(document.getMetadata().getOrDefault("source", "unknown-source"));
        String category = String.valueOf(document.getMetadata().getOrDefault("category", "general"));
        String text = document.getText() == null ? "" : document.getText();

        return """
                Source: %s
                Catégorie: %s

                %s
                """.formatted(source, category, text);
    }

    private String formatSource(Document document) {
        Object source = document.getMetadata().get("source");
        return source == null ? "unknown-source" : source.toString();
    }

    public record RagContext(
            String context,
            String sources
    ) {
    }
}