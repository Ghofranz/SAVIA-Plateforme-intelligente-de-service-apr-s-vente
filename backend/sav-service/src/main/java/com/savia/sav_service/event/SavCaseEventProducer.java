package com.savia.sav_service.event;

import com.savia.sav_service.entity.SavCase;
import com.savia.sav_service.enums.SavCaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SavCaseEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSavCaseCreated(SavCase savCase) {
        SavCaseCreatedEvent event = new SavCaseCreatedEvent(
                savCase.getId(),
                savCase.getCaseReference(),
                savCase.getCustomerId(),
                savCase.getCustomerProductId(),
                savCase.getCreatedByAuthUserId(),
                savCase.getTitle(),
                savCase.getDescription(),
                savCase.getPriority() == null ? null : savCase.getPriority().name(),
                savCase.getCreatedAt()
        );

        kafkaTemplate.send(
                KafkaTopics.SAV_CASE_CREATED,
                savCase.getCaseReference(),
                event
        );
    }
    public void publishSavCaseStatusUpdated(
            SavCase savCase,
            SavCaseStatus oldStatus,
            Long changedByAuthUserId,
            String comment
    ) {
        SavCaseStatusUpdatedEvent event = new SavCaseStatusUpdatedEvent(
                savCase.getId(),
                savCase.getCaseReference(),
                oldStatus.name(),
                savCase.getStatus().name(),
                changedByAuthUserId,
                comment,
                LocalDateTime.now()
        );

        kafkaTemplate.send(
                KafkaTopics.SAV_CASE_STATUS_UPDATED,
                savCase.getCaseReference(),
                event
        );
    }
}