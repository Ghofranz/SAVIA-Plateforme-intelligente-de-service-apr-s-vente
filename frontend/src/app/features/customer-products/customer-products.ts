import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { CustomerApiService } from '../../core/services/customer-api.service';
import { CustomerProductApiService } from '../../core/services/customer-product-api.service';
import { CustomerResponse } from '../../core/models/customer.model';
import {
CreateCustomerProductRequest,
CustomerProductResponse
} from '../../core/models/customer-product.model';

@Component({
selector: 'app-customer-products',
standalone: true,
imports: [CommonModule, ReactiveFormsModule, RouterLink],
templateUrl: './customer-products.html',
styleUrl: './customer-products.scss'
})
export class CustomerProducts implements OnInit {
private readonly formBuilder = inject(FormBuilder);
private readonly customerApiService = inject(CustomerApiService);
private readonly customerProductApiService = inject(CustomerProductApiService);

loading = false;
loadingProfile = false;
loadingProducts = false;
errorMessage = '';
successMessage = '';

customerProfile: CustomerResponse | null = null;
createdProduct: CustomerProductResponse | null = null;
products: CustomerProductResponse[] = [];

readonly form = this.formBuilder.nonNullable.group({
productName: ['', [Validators.required]],
brand: ['', [Validators.required]],
model: [''],
serialNumber: ['', [Validators.required]],
purchaseDate: [''],
warrantyEndDate: ['']
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
      },
      error: (error) => {
        this.loadingProfile = false;
        this.customerProfile = null;

        if (error.status === 404) {
          this.errorMessage = 'Tu dois d’abord compléter ton profil client avant d’ajouter un produit.';
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

  submit(): void {
    if (!this.customerProfile) {
      this.errorMessage = 'Impossible d’enregistrer un produit sans profil client.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.createdProduct = null;

    const raw = this.form.getRawValue();

    const request: CreateCustomerProductRequest = {
      productName: raw.productName,
      brand: raw.brand,
      model: raw.model || null,
      serialNumber: raw.serialNumber,
      purchaseDate: raw.purchaseDate || null,
      warrantyEndDate: raw.warrantyEndDate || null
    };

    this.customerProductApiService.createProduct(request).subscribe({
      next: (product) => {
        this.loading = false;
        this.createdProduct = product;
        this.products = [product, ...this.products];
        this.successMessage = 'Produit client enregistré avec succès.';

        this.form.reset({
          productName: '',
          brand: '',
          model: '',
          serialNumber: '',
          purchaseDate: '',
          warrantyEndDate: ''
        });
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Erreur lors de l’enregistrement du produit client.';
      }
    });
  }

  hasError(
    controlName:
      | 'productName'
      | 'brand'
      | 'model'
      | 'serialNumber'
      | 'purchaseDate'
      | 'warrantyEndDate'
  ): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }
}
