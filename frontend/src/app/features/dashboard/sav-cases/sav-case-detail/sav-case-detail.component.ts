import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SavService } from '../../../../core/services/sav.service';
import { AuthService } from '../../../../core/services/auth.service';
import { SavCase, SavCaseStatus, SavCaseStatusHistory } from '../../../../core/models/user.model';

@Component({
  selector: 'app-sav-case-detail',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatDividerModule,
    MatProgressSpinnerModule, MatSnackBarModule
  ],
  templateUrl: './sav-case-detail.component.html',
  styleUrl: './sav-case-detail.component.scss'
})
export class SavCaseDetailComponent implements OnInit {
  savCase: SavCase | null = null;
  history: SavCaseStatusHistory[] = [];
  loading = true;
  updatingStatus = false;
  assigning = false;

  statusForm: FormGroup;
  assignForm: FormGroup;

  statuses = [
    { value: 'CREATED', label: 'Créé' },
    { value: 'IN_REVIEW', label: 'En révision' },
    { value: 'WAITING_CUSTOMER', label: 'Attente client' },
    { value: 'ASSIGNED_TO_TECHNICIAN', label: 'Assigné technicien' },
    { value: 'IN_REPAIR', label: 'En réparation' },
    { value: 'RESOLVED', label: 'Résolu' },
    { value: 'CLOSED', label: 'Fermé' },
    { value: 'REJECTED', label: 'Rejeté' },
  ];

  constructor(
    private route: ActivatedRoute,
    private sav: SavService,
    public auth: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.statusForm = this.fb.group({ status: ['', Validators.required], comment: [''] });
    this.assignForm = this.fb.group({ agentId: [''], technicianId: [''] });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') || '';
    this.sav.getCaseById(id).subscribe(c => {
      this.savCase = c;
      this.statusForm.patchValue({ status: c.status });
    });
    this.sav.getCaseHistory(id).subscribe({
      next: (h: SavCaseStatusHistory[]) => { this.history = h; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  get canManage(): boolean {
    const role = this.auth.getCurrentUser()?.role;
    return role === 'AGENT' || role === 'MANAGER' || role === 'ADMIN';
  }

  getStatusLabel(status: string): string {
    return this.statuses.find(s => s.value === status)?.label || status;
  }

  updateStatus(): void {
    if (!this.savCase || this.statusForm.invalid) return;
    this.updatingStatus = true;
    const { status, comment } = this.statusForm.value;
    this.sav.updateStatus(this.savCase.id, status as SavCaseStatus, comment).subscribe({
      next: (c) => {
        this.savCase = c;
        this.sav.getCaseHistory(c.id).subscribe(h => this.history = h);
        this.snackBar.open('Statut mis à jour', 'Fermer', { duration: 3000 });
        this.updatingStatus = false;
      },
      error: () => { this.updatingStatus = false; }
    });
  }

  assignCase(): void {
    if (!this.savCase) return;
    this.assigning = true;
    const { agentId, technicianId } = this.assignForm.value;
    this.sav.assignCase(this.savCase.id, agentId || undefined, technicianId || undefined).subscribe({
      next: (c) => {
        this.savCase = c;
        this.snackBar.open('Dossier assigné', 'Fermer', { duration: 3000 });
        this.assigning = false;
      },
      error: () => { this.assigning = false; }
    });
  }
}
