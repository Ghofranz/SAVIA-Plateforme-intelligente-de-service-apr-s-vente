import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatDividerModule, MatButtonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  user: User | null = null;

  roleLabels: Record<string, string> = {
    CLIENT: 'Client', AGENT: 'Agent SAV', TECHNICIAN: 'Technicien',
    MANAGER: 'Manager', ADMIN: 'Administrateur'
  };

  constructor(private auth: AuthService) {}

  ngOnInit(): void {
    this.user = this.auth.getCurrentUser();
    if (!this.user) {
      this.auth.fetchCurrentUser().subscribe(u => this.user = u);
    }
  }

  get roleLabel(): string {
    return this.user?.role ? this.roleLabels[this.user.role] : '';
  }

  logout(): void { this.auth.logout(); }
}
