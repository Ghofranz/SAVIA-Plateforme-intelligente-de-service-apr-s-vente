import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AiAnalysisApiService } from '../../core/services/ai-analysis-api.service';
import { SavCaseApiService } from '../../core/services/sav-case-api.service';

import { AiAnalysisResponse } from '../../core/models/ai-analysis.model';
import { SavCaseResponse, UpdateSavCaseStatusRequest } from '../../core/models/sav-case.model';

@Component({
selector: 'app-sav-workspace',
standalone: true,
imports: [CommonModule, ReactiveFormsModule, RouterLink],
templateUrl: './sav-workspace.html',
styleUrl: './sav-workspace.scss'
})
export class SavWorkspace implements OnInit {
private readonly formBuilder = inject(FormBuilder);
private readonly savCaseApiService = inject(SavCaseApiService);
private readonly aiAnalysisApiService = inject(AiAnalysisApiService);

loadingCases = false;
loadingUpdate = false;
loadingAnalysis = false;

errorMessage = '';
successMessage = '';
analysisMessage = '';

selectedStatus = 'CREATED';
selectedCase: SavCaseResponse | null = null;
aiAnalysis: AiAnalysisResponse | null = null;
savCases: SavCaseResponse[] = [];

readonly statuses = [
{ value: 'CREATED', label: 'Créé' },
{ value: 'IN_REVIEW', label: 'En revue' },
{ value: 'WAITING_CUSTOMER', label: 'En attente client' },
{ value: 'ASSIGNED_TO_TECHNICIAN', label: 'Assigné technicien' },
{ value: 'IN_REPAIR', label: 'En réparation' },
{ value: 'RESOLVED', label: 'Résolu' },
{ value: 'CLOSED', label: 'Fermé' },
{ value: 'REJECTED', label: 'Rejeté' }
];

readonly form = this.formBuilder.nonNullable.group({
newStatus: ['IN_REVIEW', [Validators.required]],
comment: ['']
});

ngOnInit(): void {
    this.loadCasesByStatus(this.selectedStatus);
  }

  loadCasesByStatus(status: string): void {
    this.selectedStatus = status;
    this.selectedCase = null;
    this.aiAnalysis = null;
    this.analysisMessage = '';
    this.loadingCases = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.savCaseApiService.getSavCasesByStatus(status).subscribe({
      next: (cases) => {
        this.loadingCases = false;
        this.savCases = cases;
      },
      error: (error) => {
        this.loadingCases = false;
        this.savCases = [];
        this.errorMessage = error.error?.message || 'Erreur lors du chargement des dossiers SAV.';
      }
    });
  }

  selectCase(savCase: SavCaseResponse): void {
    this.selectedCase = savCase;
    this.successMessage = '';
    this.errorMessage = '';

    this.form.patchValue({
      newStatus: savCase.status,
      comment: ''
    });

    this.loadAiAnalysis(savCase.id);
  }

  loadAiAnalysis(savCaseId: number): void {
    this.loadingAnalysis = true;
    this.analysisMessage = '';
    this.aiAnalysis = null;

    this.aiAnalysisApiService.getAnalysisBySavCaseId(savCaseId).subscribe({
      next: (analysis) => {
        this.loadingAnalysis = false;
        this.aiAnalysis = analysis;
      },
      error: (error) => {
        this.loadingAnalysis = false;

        if (error.status === 404) {
          this.analysisMessage = 'Analyse IA non encore disponible pour ce dossier.';
          return;
        }

        this.analysisMessage = error.error?.message || 'Erreur lors du chargement de l’analyse IA.';
      }
    });
  }

  updateStatus(): void {
    if (!this.selectedCase) {
      this.errorMessage = 'Sélectionne d’abord un dossier SAV.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loadingUpdate = true;
    this.errorMessage = '';
    this.successMessage = '';

    const raw = this.form.getRawValue();

    const request: UpdateSavCaseStatusRequest = {
      newStatus: raw.newStatus,
      comment: raw.comment || null
    };

    this.savCaseApiService.updateSavCaseStatus(this.selectedCase.id, request).subscribe({
      next: (updatedCase) => {
        this.loadingUpdate = false;
        this.selectedCase = updatedCase;
        this.successMessage = 'Statut du dossier SAV mis à jour avec succès.';

        this.savCases = this.savCases.filter((item) => item.id !== updatedCase.id);

        if (updatedCase.status === this.selectedStatus) {
          this.savCases = [updatedCase, ...this.savCases];
        }

        this.form.patchValue({
          newStatus: updatedCase.status,
          comment: ''
        });
      },
      error: (error) => {
        this.loadingUpdate = false;
        this.errorMessage = error.error?.message || 'Erreur lors de la mise à jour du statut.';
      }
    });
  }

  statusLabel(status: string | null): string {
    if (!status) {
      return 'Initial';
    }

    const statusItem = this.statuses.find((item) => item.value === status);
    return statusItem?.label || status.replaceAll('_', ' ');
  }

  analysisStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'En attente',
      PROCESSING: 'Analyse en cours',
      COMPLETED: 'Analyse terminée',
      FAILED: 'Analyse échouée'
    };

    return labels[status] || status;
  }
}
