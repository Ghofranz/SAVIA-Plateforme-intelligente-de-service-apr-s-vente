export type KnowledgeArticleStatus = 'DRAFT' | 'VALIDATED' | 'ARCHIVED';

export interface KnowledgeArticleResponse {
  id: number;
savCaseId: number;
caseReference: string;
customerProductId: number | null;
sourceStatus: string;
originalProblem: string | null;
resolutionComment: string | null;
symptomSummary: string | null;
confirmedCause: string | null;
appliedSolution: string | null;
reusableKnowledge: string | null;
tags: string | null;
status: KnowledgeArticleStatus;
indexedInRag: boolean;
createdAt: string;
updatedAt: string;
}
