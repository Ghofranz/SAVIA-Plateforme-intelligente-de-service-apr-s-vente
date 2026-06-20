package com.savia.ai_service.controller;

import com.savia.ai_service.dto.AiAnalysisResponse;
import com.savia.ai_service.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-analyses")
@RequiredArgsConstructor
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    @GetMapping("/sav-case/{savCaseId}")
    public AiAnalysisResponse getAnalysisBySavCaseId(@PathVariable Long savCaseId) {
        return aiAnalysisService.getAnalysisBySavCaseId(savCaseId);
    }
}