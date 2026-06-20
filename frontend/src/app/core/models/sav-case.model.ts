export interface CreateSavCaseRequest {
  customerId: number;
customerProductId: number;
title: string;
description: string;
priority: string | null;
}

export interface UpdateSavCaseStatusRequest {
newStatus: string;
comment: string | null;
}

export interface SavCaseResponse {
id: number;
caseReference: string;
customerId: number;
customerProductId: number;
createdByAuthUserId: number;
assignedAgentAuthUserId: number | null;
assignedTechnicianAuthUserId: number | null;
title: string;
description: string;
status: string;
priority: string | null;
createdAt: string;
updatedAt: string;
closedAt: string | null;
}

export interface SavCaseStatusHistoryResponse {
id: number;
savCaseId: number;
oldStatus: string | null;
newStatus: string;
changedByAuthUserId: number;
comment: string | null;
changedAt: string;
}
