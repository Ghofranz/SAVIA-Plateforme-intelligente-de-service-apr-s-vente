import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { CustomerProductApiService } from '../../core/services/customer-product-api.service';
import { SavCaseApiService } from '../../core/services/sav-case-api.service';
import { CustomerProductResponse } from '../../core/models/customer-product.model';
import {
SavCaseResponse,
SavCaseStatusHistoryResponse
} from '../../core/models/sav-case.model';

@Component({
selector: 'app-sav-case-detail',
standalone: true,
imports: [CommonModule, RouterLink],
templateUrl: './sav-case-detail.html',
styleUrl: './sav-case-detail.scss'
})
export class SavCaseDetail implements OnInit {
private readonly route = inject(ActivatedRoute);
private readonly router = inject(Router);
private readonly savCaseApiService = inject(SavCaseApiService);
private readonly customerProductApiService = inject(CustomerProductApiService);

loading = false;
loadingHistory = false;
errorMessage = '';

savCase: SavCaseResponse | null = null;
product: CustomerProductResponse | null = null;
history: SavCaseStatusHistoryResponse[] = [];

ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id || Number.isNaN(id)) {
      this.router.navigateByUrl('/sav-cases');
      return;
    }

    this.loadSavCase(id);
    this.loadHistory(id);
  }

  loadSavCase(id: number): void {
    this.loading = true;
    this.errorMessage = '';

    this.savCaseApiService.getSavCaseById(id).subscribe({
      next: (savCase) => {
        this.loading = false;
        this.savCase = savCase;
        this.loadProduct(savCase.customerProductId);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Erreur lors du chargement du dossier SAV.';
      }
    });
  }

  loadProduct(productId: number): void {
    this.customerProductApiService.getProductById(productId).subscribe({
      next: (product) => {
        this.product = product;
      },
      error: () => {
        this.product = null;
      }
    });
  }

  loadHistory(id: number): void {
    this.loadingHistory = true;

    this.savCaseApiService.getStatusHistory(id).subscribe({
      next: (history) => {
        this.loadingHistory = false;
        this.history = history;
      },
      error: () => {
        this.loadingHistory = false;
        this.history = [];
      }
    });
  }

  statusLabel(status: string | null): string {
    if (!status) {
      return 'Initial';
    }

    return status.replaceAll('_', ' ');
  }
}
