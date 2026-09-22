import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./admin-layout/admin-layout.component').then((m) => m.AdminLayoutComponent),
    children: [
      { path: '', redirectTo: 'users', pathMatch: 'full' },
      {
        path: 'users',
        loadChildren: () =>
          import('../users/users.routes').then((m) => m.USERS_ROUTES),
      },
      {
        path: 'companies',
        loadChildren: () =>
          import('../companies/companies.routes').then((m) => m.COMPANIES_ROUTES),
      },
      {
        path: 'statistics',
        loadComponent: () =>
          import('./statistics/statistics.component').then((m) => m.StatisticsComponent),
      },
    ],
  },
];