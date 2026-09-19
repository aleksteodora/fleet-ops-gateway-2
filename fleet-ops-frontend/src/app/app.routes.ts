import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { companyUserGuard } from './core/guards/company-user.guard';

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
  path: 'admin',
  canActivate: [adminGuard],
  loadChildren: () =>
    import('./features/admin/admin.routes').then((m) => m.ADMIN_ROUTES),
},
{
  path: 'vehicle-search',
  canActivate: [companyUserGuard],
  loadChildren: () =>
    import('./features/vehicle-search/vehicle-search.routes').then((m) => m.VEHICLE_SEARCH_ROUTES),
},
];