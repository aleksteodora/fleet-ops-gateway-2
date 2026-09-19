import { Routes } from '@angular/router';

export const USER_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./user-layout/user-layout.component').then((m) => m.UserLayoutComponent),
    children: [
      { path: '', redirectTo: 'search', pathMatch: 'full' },
      {
        path: 'search',
        loadChildren: () =>
          import('../vehicle-search/vehicle-search.routes').then((m) => m.VEHICLE_SEARCH_ROUTES),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./profile/profile.component').then((m) => m.ProfileComponent),
      },
    ],
  },
];