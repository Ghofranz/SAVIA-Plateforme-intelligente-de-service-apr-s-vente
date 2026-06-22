package com.savia.ai_service.repository;

import com.savia.ai_service.entity.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    Optional<KnowledgeArticle> findBySavCaseId(Long savCaseId);

    boolean existsBySavCaseId(Long savCaseId);

    List<KnowledgeArticle> findByIndexedInRagFalse();
}