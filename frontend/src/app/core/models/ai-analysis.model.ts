export interface AiAnalysisResponse {
  id: number;
savCaseId: number;
caseReference: string;
customerId: number;
customerProductId: number;
status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
diagnosis: string | null;
possibleCauses: string | null;
recommendedActions: string | null;
ragSources: string | null;
errorMessage: string | null;
createdAt: string;
updatedAt: string;
}
