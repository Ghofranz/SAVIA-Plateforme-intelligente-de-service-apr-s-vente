package com.savia.sav_service.repository;

import com.savia.sav_service.entity.SavCase;
import com.savia.sav_service.enums.SavCaseStatus;
import com.savia.sav_service.enums.SavPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavCaseRepository extends JpaRepository<SavCase, Long> {

    Optional<SavCase> findByCaseReference(String caseReference);

    List<SavCase> findByCustomerId(Long customerId);

    List<SavCase> findByCustomerProductId(Long customerProductId);

    List<SavCase> findByStatus(SavCaseStatus status);

    List<SavCase> findByPriority(SavPriority priority);

    List<SavCase> findByAssignedAgentAuthUserId(Long assignedAgentAuthUserId);

    List<SavCase> findByAssignedTechnicianAuthUserId(Long assignedTechnicianAuthUserId);

    boolean existsByCaseReference(String caseReference);
}