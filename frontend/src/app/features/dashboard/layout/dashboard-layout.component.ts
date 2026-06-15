import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [
    CommonModule, RouterOutlet, RouterLink, RouterLinkActive,
    MatSidenavModule, MatToolbarModule, MatListModule,
    MatIconModule, MatButtonModule, MatMenuModule, MatDividerModule
  ],
  templateUrl: './dashboard-layout.component.html',
  styleUrls: ['./dashboard-layout.component.scss']
})
export class DashboardLayoutComponent implements OnInit {
  currentUser: User | null = null;

  navItems = [
    { label: 'Tableau de bord', icon: 'dashboard', route: '/dashboard', exact: true, roles: ['CLIENT', 'AGENT', 'TECHNICIAN', 'MANAGER', 'ADMIN'] },
    { label: 'Mes demandes SAV', icon: 'assignment', route: '/dashboard/sav-cases', exact: false, roles: ['CLIENT'] },
    { label: 'Dossiers SAV', icon: 'folder_open', route: '/dashboard/sav-cases', exact: false, roles: ['AGENT', 'TECHNICIAN', 'MANAGER', 'ADMIN'] },
    { label: 'Clients', icon: 'people', route: '/dashboard/customers', exact: false, roles: ['AGENT', 'MANAGER', 'ADMIN'] },
    { label: 'Mon profil', icon: 'person', route: '/dashboard/profile', exact: false, roles: ['CLIENT', 'AGENT', 'TECHNICIAN', 'MANAGER', 'ADMIN'] }
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => this.currentUser = user);
  }

  get filteredNavItems() {
    const role = this.currentUser?.role;
    if (!role) return [];
    return this.navItems.filter(item => item.roles.includes(role));
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getRoleLabel(role: string): string {
    const labels: Record<string, string> = {
      CLIENT: 'Client', AGENT: 'Agent SAV', TECHNICIAN: 'Technicien',
      MANAGER: 'Responsable', ADMIN: 'Administrateur'
    };
    return labels[role] || role;
  }

  getUserInitials(): string {
    if (!this.currentUser) return '?';
    return `${this.currentUser.firstname[0]}${this.currentUser.lastname[0]}`.toUpperCase();
  }
}
