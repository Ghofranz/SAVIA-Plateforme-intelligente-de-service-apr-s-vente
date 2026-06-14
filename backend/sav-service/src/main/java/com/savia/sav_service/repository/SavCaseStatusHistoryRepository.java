package com.savia.sav_service.repository;

import com.savia.sav_service.entity.SavCaseStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavCaseStatusHistoryRepository extends JpaRepository<SavCaseStatusHistory, Long> {

    List<SavCaseStatusHistory> findBySavCaseIdOrderByChangedAtAsc(Long savCaseId);
}