import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { CustomerApiService } from '../../core/services/customer-api.service';
import { CreateCustomerRequest, CustomerResponse } from '../../core/models/customer.model';

@Component({
selector: 'app-customers',
standalone: true,
imports: [CommonModule, ReactiveFormsModule, RouterLink],
templateUrl: './customers.html',
styleUrl: './customers.scss'
})
export class Customers implements OnInit {
private readonly formBuilder = inject(FormBuilder);
private readonly customerApiService = inject(CustomerApiService);

loading = false;
loadingProfile = false;
errorMessage = '';
successMessage = '';
customerProfile: CustomerResponse | null = null;

readonly form = this.formBuilder.nonNullable.group({
firstname: ['', [Validators.required]],
lastname: ['', [Validators.required]],
email: ['', [Validators.required, Validators.email]],
phoneNumber: ['', [Validators.required]],
address: ['', [Validators.required]]
});

ngOnInit(): void {
    this.loadMyProfile();
  }

  loadMyProfile(): void {
    this.loadingProfile = true;
    this.errorMessage = '';

    this.customerApiService.getMyCustomerProfile().subscribe({
      next: (customer) => {
        this.loadingProfile = false;
        this.customerProfile = customer;

        this.form.patchValue({
          firstname: customer.firstname,
          lastname: customer.lastname,
          email: customer.email,
          phoneNumber: customer.phoneNumber,
          address: customer.address
        });
      },
      error: (error) => {
        this.loadingProfile = false;

        if (error.status === 404) {
          this.customerProfile = null;
          return;
        }

        this.errorMessage = error.error?.message || 'Erreur lors du chargement du profil client.';
      }
    });
  }

  submit(): void {
    if (this.customerProfile) {
      this.errorMessage = 'Le profil existe déjà. La modification sera ajoutée dans une prochaine étape.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: CreateCustomerRequest = this.form.getRawValue();

    this.customerApiService.createCustomer(request).subscribe({
      next: (customer) => {
        this.loading = false;
        this.customerProfile = customer;
        this.successMessage = 'Profil client enregistré avec succès.';
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de l’enregistrement du profil client.';
      }
    });
  }

  hasError(controlName: 'firstname' | 'lastname' | 'email' | 'phoneNumber' | 'address'): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }
}
