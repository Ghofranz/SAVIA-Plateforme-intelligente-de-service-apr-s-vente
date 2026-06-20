package com.savia.ai_service.repository;

import com.savia.ai_service.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, Long> {

    Optional<AiAnalysis> findBySavCaseId(Long savCaseId);

    boolean existsBySavCaseId(Long savCaseId);
}