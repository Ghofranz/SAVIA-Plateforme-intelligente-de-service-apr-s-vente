import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { RegisterRequest, UserRole } from '../../../core/models/auth.model';

@Component({
selector: 'app-register',
standalone: true,
imports: [CommonModule, ReactiveFormsModule, RouterLink],
templateUrl: './register.html',
styleUrl: './register.scss'
})
export class Register {
private readonly formBuilder = inject(FormBuilder);
private readonly authService = inject(AuthService);
private readonly router = inject(Router);

loading = false;
errorMessage = '';

readonly roles: { value: UserRole; label: string }[] = [
{ value: 'CLIENT', label: 'Client' },
{ value: 'AGENT', label: 'Agent SAV' },
{ value: 'TECHNICIAN', label: 'Technicien' },
{ value: 'MANAGER', label: 'Manager' },
{ value: 'ADMIN', label: 'Administrateur' }
];

readonly form = this.formBuilder.nonNullable.group({
firstname: ['', [Validators.required]],
lastname: ['', [Validators.required]],
email: ['', [Validators.required, Validators.email]],
password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['CLIENT' as UserRole, [Validators.required]]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request: RegisterRequest = this.form.getRawValue();

    this.authService.register(request).subscribe({
      next: () => {
        this.authService.loadCurrentUser().subscribe({
          next: () => this.router.navigateByUrl('/dashboard'),
          error: () => {
            this.loading = false;
            this.errorMessage = 'Inscription réussie, mais impossible de charger le profil utilisateur.';
          }
        });
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de la création du compte.';
      }
    });
  }

  hasError(controlName: 'firstname' | 'lastname' | 'email' | 'password' | 'role'): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }
}
