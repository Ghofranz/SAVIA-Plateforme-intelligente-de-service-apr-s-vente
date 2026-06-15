import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
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
export class Customers {
private readonly formBuilder = inject(FormBuilder);
private readonly customerApiService = inject(CustomerApiService);

loading = false;
errorMessage = '';
successMessage = '';
createdCustomer: CustomerResponse | null = null;

readonly form = this.formBuilder.nonNullable.group({
firstname: ['', [Validators.required]],
lastname: ['', [Validators.required]],
email: ['', [Validators.required, Validators.email]],
phoneNumber: ['', [Validators.required]],
address: ['', [Validators.required]]
});

submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.createdCustomer = null;

    const request: CreateCustomerRequest = this.form.getRawValue();

    this.customerApiService.createCustomer(request).subscribe({
      next: (customer) => {
        this.loading = false;
        this.createdCustomer = customer;
this.successMessage = 'Profil client enregistré avec succès.';
this.form.reset();
      },
      error: (error) => {
        this.loading = false;
this.errorMessage = error.error?.message || 'Erreur lors de l’enregistrement du profil client.';      }
    });
  }

  hasError(controlName: 'firstname' | 'lastname' | 'email' | 'phoneNumber' | 'address'): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }
}
