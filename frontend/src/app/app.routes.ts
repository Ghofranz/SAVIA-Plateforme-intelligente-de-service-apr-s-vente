import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/layout/dashboard-layout.component').then(m => m.DashboardLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'sav-cases',
        loadComponent: () => import('./features/dashboard/sav-cases/sav-case-list/sav-case-list.component').then(m => m.SavCaseListComponent)
      },
      {
        path: 'sav-cases/new',
        loadComponent: () => import('./features/dashboard/sav-cases/sav-case-create/sav-case-create.component').then(m => m.SavCaseCreateComponent)
      },
      {
        path: 'sav-cases/:id',
        loadComponent: () => import('./features/dashboard/sav-cases/sav-case-detail/sav-case-detail.component').then(m => m.SavCaseDetailComponent)
      },
      {
        path: 'customers',
        loadComponent: () => import('./features/dashboard/customers/customer-list/customer-list.component').then(m => m.CustomerListComponent)
      },
      {
        path: 'customers/:id',
        loadComponent: () => import('./features/dashboard/customers/customer-detail/customer-detail.component').then(m => m.CustomerDetailComponent)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/dashboard/profile/profile.component').then(m => m.ProfileComponent)
      }
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
