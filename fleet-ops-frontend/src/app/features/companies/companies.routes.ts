import { Routes } from '@angular/router';

export const COMPANIES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./companies-list/companies-list.component').then((m) => m.CompaniesListComponent),
  },
];