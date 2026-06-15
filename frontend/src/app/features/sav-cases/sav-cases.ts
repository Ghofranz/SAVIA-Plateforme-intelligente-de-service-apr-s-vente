import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { CustomerApiService } from '../../core/services/customer-api.service';
import { CustomerProductApiService } from '../../core/services/customer-product-api.service';
import { SavCaseApiService } from '../../core/services/sav-case-api.service';

import { CustomerResponse } from '../../core/models/customer.model';
import { CustomerProductResponse } from '../../core/models/customer-product.model';
import { CreateSavCaseRequest, SavCaseResponse } from '../../core/models/sav-case.model';

@Component({
selector: 'app-sav-cases',
standalone: true,
imports: [CommonModule, ReactiveFormsModule, RouterLink],
templateUrl: './sav-cases.html',
styleUrl: './sav-cases.scss'
})
export class SavCases implements OnInit {
private readonly formBuilder = inject(FormBuilder);
private readonly customerApiService = inject(CustomerApiService);
private readonly customerProductApiService = inject(CustomerProductApiService);
private readonly savCaseApiService = inject(SavCaseApiService);

loadingProfile = false;
loadingProducts = false;
loadingCases = false;
loading = false;

errorMessage = '';
successMessage = '';

customerProfile: CustomerResponse | null = null;
products: CustomerProductResponse[] = [];
savCases: SavCaseResponse[] = [];
createdCase: SavCaseResponse | null = null;

readonly priorities = [
{ value: '', label: 'Non spécifiée' },
{ value: 'LOW', label: 'Faible' },
{ value: 'NORMAL', label: 'Normale' },
{ value: 'HIGH', label: 'Haute' },
{ value: 'URGENT', label: 'Urgente' }
];

readonly form = this.formBuilder.nonNullable.group({
customerProductId: [0, [Validators.required, Validators.min(1)]],
    title: ['', [Validators.required]],
    description: ['', [Validators.required]],
    priority: ['']
  });

  ngOnInit(): void {
    this.loadMyCustomerProfile();
  }

  loadMyCustomerProfile(): void {
    this.loadingProfile = true;
    this.errorMessage = '';

    this.customerApiService.getMyCustomerProfile().subscribe({
      next: (customer) => {
        this.loadingProfile = false;
        this.customerProfile = customer;
        this.loadMyProducts();
        this.loadMySavCases();
      },
      error: (error) => {
        this.loadingProfile = false;
        this.customerProfile = null;

        if (error.status === 404) {
          this.errorMessage = 'Tu dois d’abord compléter ton profil client avant de créer un dossier SAV.';
          return;
        }

        this.errorMessage = error.error?.message || 'Erreur lors du chargement du profil client.';
      }
    });
  }

  loadMyProducts(): void {
    this.loadingProducts = true;

    this.customerProductApiService.getMyProducts().subscribe({
      next: (products) => {
        this.loadingProducts = false;
        this.products = products;
      },
      error: (error) => {
        this.loadingProducts = false;
        this.errorMessage = error.error?.message || 'Erreur lors du chargement des produits.';
      }
    });
  }

  loadMySavCases(): void {
    if (!this.customerProfile) {
      return;
    }

    this.loadingCases = true;

    this.savCaseApiService.getSavCasesByCustomerId(this.customerProfile.id).subscribe({
      next: (cases) => {
        this.loadingCases = false;
        this.savCases = cases;
      },
      error: (error) => {
        this.loadingCases = false;
        this.errorMessage = error.error?.message || 'Erreur lors du chargement des dossiers SAV.';
      }
    });
  }

  submit(): void {
    if (!this.customerProfile) {
      this.errorMessage = 'Impossible de créer un dossier SAV sans profil client.';
      return;
    }

    if (this.products.length === 0) {
      this.errorMessage = 'Tu dois d’abord enregistrer au moins un produit client.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.createdCase = null;

    const raw = this.form.getRawValue();

    const request: CreateSavCaseRequest = {
      customerId: this.customerProfile.id,
      customerProductId: Number(raw.customerProductId),
      title: raw.title,
      description: raw.description,
      priority: raw.priority || null
    };

    this.savCaseApiService.createSavCase(request).subscribe({
      next: (savCase) => {
        this.loading = false;
        this.createdCase = savCase;
        this.savCases = [savCase, ...this.savCases];
        this.successMessage = 'Dossier SAV créé avec succès.';

        this.form.reset({
          customerProductId: 0,
          title: '',
          description: '',
          priority: ''
        });
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de la création du dossier SAV.';
      }
    });
  }

  productLabel(productId: number): string {
    const product = this.products.find((item) => item.id === productId);

    if (!product) {
      return `Produit #${productId}`;
    }

    return `${product.productName} — ${product.brand} — ${product.serialNumber}`;
  }

  statusLabel(status: string): string {
    return status.replaceAll('_', ' ');
  }

  hasError(controlName: 'customerProductId' | 'title' | 'description' | 'priority'): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }
}
