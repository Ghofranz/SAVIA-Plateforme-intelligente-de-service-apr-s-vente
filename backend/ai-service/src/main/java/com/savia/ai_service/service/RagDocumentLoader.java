package com.savia.ai_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagDocumentLoader {

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resourcePatternResolver;

    @EventListener(ApplicationReadyEvent.class)
    public void loadStaticRagDocuments() {
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:/rag-docs/*.md");

            List<Document> documents = new ArrayList<>();

            for (Resource resource : resources) {
                String filename = resource.getFilename();

                if (filename == null) {
                    continue;
                }

                String content = new String(
                        resource.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8
                );

                if (content.isBlank()) {
                    continue;
                }

                Document document = Document.builder()
                        .text(content)
                        .metadata("source", filename)
                        .metadata("type", "static_sav_document")
                        .metadata("category", resolveCategory(filename))
                        .build();

                documents.add(document);
            }

            if (documents.isEmpty()) {
                log.warn("No static RAG documents found in classpath:/rag-docs/");
                return;
            }

            vectorStore.add(documents);

            log.info("Loaded {} static RAG documents into vector store.", documents.size());
        } catch (Exception exception) {
            log.error("Failed to load static RAG documents.", exception);
        }
    }

    private String resolveCategory(String filename) {
        if (filename.contains("warranty")) {
            return "warranty";
        }

        if (filename.contains("screen")) {
            return "screen";
        }

        if (filename.contains("battery")) {
            return "battery";
        }

        if (filename.contains("network")) {
            return "network";
        }

        if (filename.contains("repair")) {
            return "repair_workflow";
        }

        return "general";
    }
}