import { Routes } from '@angular/router';

export const VEHICLE_SEARCH_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./vehicle-search.component').then((m) => m.VehicleSearchComponent),
  },
];