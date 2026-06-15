import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { LoginRequest } from '../../../core/models/auth.model';

@Component({
selector: 'app-login',
standalone: true,
imports: [CommonModule, ReactiveFormsModule, RouterLink],
templateUrl: './login.html',
styleUrl: './login.scss'
})
export class Login {
private readonly formBuilder = inject(FormBuilder);
private readonly authService = inject(AuthService);
private readonly router = inject(Router);

loading = false;
errorMessage = '';

readonly form = this.formBuilder.nonNullable.group({
email: ['', [Validators.required, Validators.email]],
password: ['', [Validators.required]]
});

submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request: LoginRequest = this.form.getRawValue();

    this.authService.login(request).subscribe({
      next: () => {
        this.authService.loadCurrentUser().subscribe({
          next: () => this.router.navigateByUrl('/dashboard'),
          error: () => {
            this.loading = false;
            this.errorMessage = 'Connexion réussie, mais impossible de charger le profil utilisateur.';
          }
        });
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Email ou mot de passe incorrect.';
      }
    });
  }

  hasError(controlName: 'email' | 'password'): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }
}
