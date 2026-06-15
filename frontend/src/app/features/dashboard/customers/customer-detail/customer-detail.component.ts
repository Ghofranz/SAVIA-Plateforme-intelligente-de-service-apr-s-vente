import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CustomerService } from '../../../../core/services/customer.service';
import { Customer, CustomerProduct } from '../../../../core/models/user.model';

@Component({
  selector: 'app-customer-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatButtonModule, MatIconModule, MatTableModule, MatDividerModule, MatProgressSpinnerModule],
  templateUrl: './customer-detail.component.html',
  styleUrl: './customer-detail.component.scss'
})
export class CustomerDetailComponent implements OnInit {
  customer: Customer | null = null;
  products: CustomerProduct[] = [];
  loading = true;
  productColumns = ['product', 'brand', 'model', 'serial', 'warranty'];

  constructor(private route: ActivatedRoute, private customerService: CustomerService) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.customerService.getCustomerById(id).subscribe(c => {
      this.customer = c;
      this.customerService.getProductsByCustomer(id).subscribe({
        next: (p) => { this.products = p; this.loading = false; },
        error: () => { this.loading = false; }
      });
    });
  }
}
