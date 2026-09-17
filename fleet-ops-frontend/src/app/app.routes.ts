import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'landing', pathMatch: 'full' },
  {
    path: 'landing',
    loadChildren: () =>
      import('./features/landing/landing.routes').then((m) => m.LANDING_ROUTES),
  },
  {
    path: 'login',
    loadChildren: () =>
      import('./features/login/login.routes').then((m) => m.LOGIN_ROUTES),
  },
  {
    path: 'users',
    canActivate: [adminGuard],
    loadChildren: () =>
      import('./features/users/users.routes').then((m) => m.USERS_ROUTES),
  },
  {
  path: 'companies',
  canActivate: [adminGuard],
  loadChildren: () =>
    import('./features/companies/companies.routes').then((m) => m.COMPANIES_ROUTES),
},
];