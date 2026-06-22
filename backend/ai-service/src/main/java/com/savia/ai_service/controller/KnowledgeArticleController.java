package com.savia.ai_service.controller;

import com.savia.ai_service.dto.KnowledgeArticleResponse;
import com.savia.ai_service.service.KnowledgeArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-articles")
@RequiredArgsConstructor
public class KnowledgeArticleController {

    private final KnowledgeArticleService knowledgeArticleService;

    @GetMapping
    public List<KnowledgeArticleResponse> getAllArticles() {
        return knowledgeArticleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public KnowledgeArticleResponse getArticleById(@PathVariable Long id) {
        return knowledgeArticleService.getArticleById(id);
    }

    @PutMapping("/{id}/validate")
    public KnowledgeArticleResponse validateArticle(@PathVariable Long id) {
        return knowledgeArticleService.validateArticle(id);
    }

    @PutMapping("/{id}/archive")
    public KnowledgeArticleResponse archiveArticle(@PathVariable Long id) {
        return knowledgeArticleService.archiveArticle(id);
    }
}