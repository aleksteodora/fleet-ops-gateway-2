import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const user = authService.currentUser();

  if (user) {
    req = req.clone({
      setHeaders: {
        'X-User-Id': user.userId.toString(),
        ...(user.companyId !== null && { 'X-Company-Id': user.companyId.toString() }),
      },
    });
  }

  return next(req);
};