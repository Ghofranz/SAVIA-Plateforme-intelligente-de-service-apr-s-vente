package com.savia.sav_service.dto;

public record AssignSavCaseRequest(
        Long assignedAgentAuthUserId,
        Long assignedTechnicianAuthUserId
) {
}