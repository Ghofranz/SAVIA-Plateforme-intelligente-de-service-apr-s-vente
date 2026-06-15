import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { UserRole } from '../../core/models/auth.model';

@Component({
selector: 'app-dashboard',
standalone: true,
imports: [CommonModule, RouterLink],
templateUrl: './dashboard.html',
styleUrl: './dashboard.scss'
})
export class Dashboard {
readonly authService = inject(AuthService);

private readonly router = inject(Router);

logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }

  roleLabel(role: UserRole): string {
    const labels: Record<UserRole, string> = {
      CLIENT: 'Client',
      AGENT: 'Agent SAV',
      TECHNICIAN: 'Technicien',
      MANAGER: 'Manager',
      ADMIN: 'Administrateur'
    };

    return labels[role];
  }
}
