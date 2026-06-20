package com.savia.ai_service.event;

import com.savia.ai_service.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SavCaseCreatedConsumer {

    private final AiAnalysisService aiAnalysisService;

    @KafkaListener(
            topics = "sav.case.created",
            groupId = "ai-service-group"
    )
    public void consumeSavCaseCreated(SavCaseCreatedEvent event) {
        log.info("Received sav.case.created event for SAV case id: {}", event.savCaseId());

        aiAnalysisService.createPendingAnalysis(event);

        log.info("Pending AI analysis created for SAV case id: {}", event.savCaseId());
    }
}