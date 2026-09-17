import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.currentUser();

  if (!user) {
    router.navigate(['/login']);
    return false;
  }

  if (user.role !== 'ADMIN') {
    router.navigate(['/']);
    return false;
  }

  return true;
};