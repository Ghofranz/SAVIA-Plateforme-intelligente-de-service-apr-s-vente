import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';

import { KnowledgeBase } from './features/knowledge-base/knowledge-base';

export const routes: Routes = [
{
path: 'login',
loadComponent: () =>
      import('./features/auth/login/login').then((m) => m.Login)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register').then((m) => m.Register)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard').then((m) => m.Dashboard)
  },
  {
    path: 'customers',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/customers/customers').then((m) => m.Customers)
  },
{
    path: 'customer-products',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/customer-products/customer-products').then((m) => m.CustomerProducts)
  },
{
    path: 'sav-cases',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/sav-cases/sav-cases').then((m) => m.SavCases)
  },
{
  path: 'sav-cases/:id',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/sav-case-detail/sav-case-detail').then((m) => m.SavCaseDetail)
},
{
  path: 'sav-workspace',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/sav-workspace/sav-workspace').then((m) => m.SavWorkspace)
},
{
  path: 'knowledge-base',
  component: KnowledgeBase,
  canActivate: [authGuard]
},
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard'
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
