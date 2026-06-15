import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SavService } from '../../../core/services/sav.service';
import { AuthService } from '../../../core/services/auth.service';
import { SavCase } from '../../../core/models/user.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  loading = true;
  cases: SavCase[] = [];
  stats = { total: 0, open: 0, resolved: 0, urgent: 0 };

  constructor(private sav: SavService, public auth: AuthService) {}

  ngOnInit(): void {
    this.sav.getAllCases().subscribe({
      next: (cases) => {
        this.cases = cases;
        this.stats.total = cases.length;
        this.stats.open = cases.filter(c => !['RESOLVED','CLOSED','REJECTED'].includes(c.status)).length;
        this.stats.resolved = cases.filter(c => c.status === 'RESOLVED' || c.status === 'CLOSED').length;
        this.stats.urgent = cases.filter(c => c.priority === 'URGENT').length;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  get recentCases(): SavCase[] { return this.cases.slice(0, 5); }
  get user() { return this.auth.getCurrentUser(); }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      CREATED: 'Créé', IN_REVIEW: 'En révision', WAITING_CUSTOMER: 'Attente client',
      ASSIGNED_TO_TECHNICIAN: 'Assigné tech.', IN_REPAIR: 'En réparation',
      RESOLVED: 'Résolu', CLOSED: 'Fermé', REJECTED: 'Rejeté'
    };
    return map[status] || status;
  }
}
