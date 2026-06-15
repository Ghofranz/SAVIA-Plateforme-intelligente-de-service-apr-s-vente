import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SavService } from '../../../../core/services/sav.service';
import { SavCase, SavCaseStatus } from '../../../../core/models/user.model';

@Component({
  selector: 'app-sav-case-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatTableModule, MatCardModule, MatButtonModule, MatIconModule,
    MatSelectModule, MatFormFieldModule, MatProgressSpinnerModule, MatTooltipModule
  ],
  templateUrl: './sav-case-list.component.html',
  styleUrl: './sav-case-list.component.scss'
})
export class SavCaseListComponent implements OnInit {
  cases: SavCase[] = [];
  filteredCases: SavCase[] = [];
  loading = true;
  displayedColumns = ['reference', 'title', 'status', 'priority', 'date', 'actions'];
  statusFilter = new FormControl<SavCaseStatus | ''>('');

  statuses = [
    { value: '', label: 'Tous les statuts' },
    { value: 'CREATED', label: 'Créé' },
    { value: 'IN_REVIEW', label: 'En révision' },
    { value: 'WAITING_CUSTOMER', label: 'Attente client' },
    { value: 'ASSIGNED_TO_TECHNICIAN', label: 'Assigné technicien' },
    { value: 'IN_REPAIR', label: 'En réparation' },
    { value: 'RESOLVED', label: 'Résolu' },
    { value: 'CLOSED', label: 'Fermé' },
    { value: 'REJECTED', label: 'Rejeté' },
  ];

  statusLabels: Record<string, string> = {
    CREATED: 'Créé', IN_REVIEW: 'En révision', WAITING_CUSTOMER: 'Attente client',
    ASSIGNED_TO_TECHNICIAN: 'Assigné tech.', IN_REPAIR: 'En réparation',
    RESOLVED: 'Résolu', CLOSED: 'Fermé', REJECTED: 'Rejeté'
  };

  constructor(private sav: SavService) {}

  ngOnInit(): void {
    this.sav.getAllCases().subscribe({
      next: (cases) => { this.cases = cases; this.filteredCases = cases; this.loading = false; },
      error: () => { this.loading = false; }
    });

    this.statusFilter.valueChanges.subscribe(status => {
      this.filteredCases = status ? this.cases.filter(c => c.status === status) : this.cases;
    });
  }
}
