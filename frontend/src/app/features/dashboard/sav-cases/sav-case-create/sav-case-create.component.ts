import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SavService } from '../../../../core/services/sav.service';
import { CustomerService } from '../../../../core/services/customer.service';
import { AuthService } from '../../../../core/services/auth.service';
import { CustomerProduct } from '../../../../core/models/user.model';

@Component({
  selector: 'app-sav-case-create',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatSnackBarModule
  ],
  templateUrl: './sav-case-create.component.html',
  styleUrl: './sav-case-create.component.scss'
})
export class SavCaseCreateComponent implements OnInit {
  form: FormGroup;
  loading = false;
  loadingProducts = false;
  products: CustomerProduct[] = [];
  error = '';
  customerId: number | null = null;

  priorities = [
    { value: 'LOW', label: 'Basse' },
    { value: 'MEDIUM', label: 'Moyenne' },
    { value: 'HIGH', label: 'Haute' },
    { value: 'URGENT', label: 'Urgente' },
  ];

  constructor(
    private fb: FormBuilder,
    private sav: SavService,
    private customerService: CustomerService,
    public auth: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      priority: ['MEDIUM', Validators.required],
      customerProductId: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    if (user) {
      this.loadingProducts = true;
      this.customerService.getCustomerByAuthUserId(user.id).subscribe({
        next: (customer) => {
          this.customerId = customer.id;
          this.customerService.getProductsByCustomer(customer.id).subscribe({
            next: (products) => { this.products = products; this.loadingProducts = false; },
            error: () => { this.loadingProducts = false; }
          });
        },
        error: () => { this.loadingProducts = false; }
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid || !this.customerId) return;
    this.loading = true;
    this.error = '';
    const user = this.auth.getCurrentUser();

    this.sav.createCase({
      ...this.form.value,
      customerId: this.customerId,
      createdByAuthUserId: user?.id
    }).subscribe({
      next: (c) => {
        this.snackBar.open('Dossier créé avec succès', 'Fermer', { duration: 3000 });
        this.router.navigate(['/dashboard/sav-cases', c.id]);
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors de la création';
        this.loading = false;
      }
    });
  }
}
