package com.savia.sav_service.service;

import com.savia.sav_service.dto.AssignSavCaseRequest;
import com.savia.sav_service.dto.CreateSavCaseRequest;
import com.savia.sav_service.dto.SavCaseResponse;
import com.savia.sav_service.dto.SavCaseStatusHistoryResponse;
import com.savia.sav_service.dto.UpdateSavCaseStatusRequest;
import com.savia.sav_service.entity.SavCase;
import com.savia.sav_service.entity.SavCaseStatusHistory;
import com.savia.sav_service.enums.SavCaseStatus;
import com.savia.sav_service.event.SavCaseEventProducer;
import com.savia.sav_service.exception.ResourceNotFoundException;
import com.savia.sav_service.repository.SavCaseRepository;
import com.savia.sav_service.repository.SavCaseStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavCaseService {

    private final SavCaseRepository savCaseRepository;
    private final SavCaseStatusHistoryRepository statusHistoryRepository;
    private final SavCaseEventProducer savCaseEventProducer;

    @Transactional
    public SavCaseResponse createSavCase(CreateSavCaseRequest request, Long createdByAuthUserId) {
        SavCase savCase = SavCase.builder()
                .caseReference(generateCaseReference())
                .customerId(request.customerId())
                .customerProductId(request.customerProductId())
                .createdByAuthUserId(createdByAuthUserId)
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .status(SavCaseStatus.CREATED)
                .build();

        SavCase savedSavCase = savCaseRepository.save(savCase);

        SavCaseStatusHistory history = SavCaseStatusHistory.builder()
                .savCase(savedSavCase)
                .oldStatus(null)
                .newStatus(SavCaseStatus.CREATED)
                .changedByAuthUserId(createdByAuthUserId)
                .comment("SAV case created")
                .build();

        statusHistoryRepository.save(history);

        savCaseEventProducer.publishSavCaseCreated(savedSavCase);

        return mapToResponse(savedSavCase);
    }

    @Transactional(readOnly = true)
    public SavCaseResponse getSavCaseById(Long id) {
        SavCase savCase = savCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SAV case not found"));

        return mapToResponse(savCase);
    }

    @Transactional(readOnly = true)
    public SavCaseResponse getSavCaseByReference(String caseReference) {
        SavCase savCase = savCaseRepository.findByCaseReference(caseReference)
                .orElseThrow(() -> new ResourceNotFoundException("SAV case not found"));

        return mapToResponse(savCase);
    }

    @Transactional(readOnly = true)
    public List<SavCaseResponse> getSavCasesByCustomerId(Long customerId) {
        return savCaseRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SavCaseResponse> getSavCasesByProductId(Long customerProductId) {
        return savCaseRepository.findByCustomerProductId(customerProductId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SavCaseResponse> getSavCasesByStatus(SavCaseStatus status) {
        return savCaseRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public SavCaseResponse assignSavCase(Long id, AssignSavCaseRequest request) {
        SavCase savCase = savCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SAV case not found"));

        savCase.setAssignedAgentAuthUserId(request.assignedAgentAuthUserId());
        savCase.setAssignedTechnicianAuthUserId(request.assignedTechnicianAuthUserId());

        SavCase savedSavCase = savCaseRepository.save(savCase);

        return mapToResponse(savedSavCase);
    }

    @Transactional
    public SavCaseResponse updateSavCaseStatus(
            Long id,
            UpdateSavCaseStatusRequest request,
            Long changedByAuthUserId
    ) {
        SavCase savCase = savCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SAV case not found"));

        SavCaseStatus oldStatus = savCase.getStatus();
        SavCaseStatus newStatus = request.newStatus();

        if (oldStatus == newStatus) {
            return mapToResponse(savCase);
        }

        savCase.setStatus(newStatus);

        if (newStatus == SavCaseStatus.CLOSED || newStatus == SavCaseStatus.REJECTED) {
            savCase.setClosedAt(LocalDateTime.now());
        }

        SavCase savedSavCase = savCaseRepository.save(savCase);

        SavCaseStatusHistory history = SavCaseStatusHistory.builder()
                .savCase(savedSavCase)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedByAuthUserId(changedByAuthUserId)
                .comment(request.comment())
                .build();

        statusHistoryRepository.save(history);

        savCaseEventProducer.publishSavCaseStatusUpdated(
                savedSavCase,
                oldStatus,
                changedByAuthUserId,
                request.comment()
        );

        return mapToResponse(savedSavCase);
    }

    @Transactional(readOnly = true)
    public List<SavCaseStatusHistoryResponse> getStatusHistory(Long savCaseId) {
        if (!savCaseRepository.existsById(savCaseId)) {
            throw new ResourceNotFoundException("SAV case not found");
        }

        return statusHistoryRepository.findBySavCaseIdOrderByChangedAtAsc(savCaseId)
                .stream()
                .map(this::mapHistoryToResponse)
                .toList();
    }

    private String generateCaseReference() {
        String reference;

        do {
            reference = "SAV-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        } while (savCaseRepository.existsByCaseReference(reference));

        return reference;
    }

    private SavCaseResponse mapToResponse(SavCase savCase) {
        return new SavCaseResponse(
                savCase.getId(),
                savCase.getCaseReference(),
                savCase.getCustomerId(),
                savCase.getCustomerProductId(),
                savCase.getCreatedByAuthUserId(),
                savCase.getAssignedAgentAuthUserId(),
                savCase.getAssignedTechnicianAuthUserId(),
                savCase.getTitle(),
                savCase.getDescription(),
                savCase.getStatus(),
                savCase.getPriority(),
                savCase.getCreatedAt(),
                savCase.getUpdatedAt(),
                savCase.getClosedAt()
        );
    }

    private SavCaseStatusHistoryResponse mapHistoryToResponse(SavCaseStatusHistory history) {
        return new SavCaseStatusHistoryResponse(
                history.getId(),
                history.getSavCase().getId(),
                history.getOldStatus(),
                history.getNewStatus(),
                history.getChangedByAuthUserId(),
                history.getComment(),
                history.getChangedAt()
        );
    }
}