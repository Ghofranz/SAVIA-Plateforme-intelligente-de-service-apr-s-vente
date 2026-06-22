package com.savia.ai_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savia.ai_service.service.KnowledgeArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SavCaseStatusUpdatedConsumer {

    private final ObjectMapper objectMapper;
    private final KnowledgeArticleService knowledgeArticleService;

    @KafkaListener(
            topics = "sav.case.status.updated",
            groupId = "ai-service-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void consumeSavCaseStatusUpdated(String payload) {
        try {
            SavCaseStatusUpdatedEvent event = objectMapper.readValue(
                    payload,
                    SavCaseStatusUpdatedEvent.class
            );

            log.info(
                    "Received sav.case.status.updated event for SAV case id: {}, new status: {}",
                    event.savCaseId(),
                    event.newStatus()
            );

            knowledgeArticleService.handleSavCaseStatusUpdated(event);
        } catch (Exception exception) {
            log.error("Failed to consume sav.case.status.updated event.", exception);
        }
    }
}