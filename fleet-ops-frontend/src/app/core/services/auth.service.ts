import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest } from '../models/login-request.model';
import { LoginResponse } from '../models/login-response.model';
import { CurrentUser } from '../models/current-user.model';
import { User } from '../models/user.model';

const STORAGE_KEY = 'fleetops_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  readonly currentUser = signal<CurrentUser | null>(this.readFromStorage());
  readonly isAuthenticated = signal<boolean>(this.readFromStorage() !== null);

  login(request: LoginRequest): Observable<CurrentUser> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      switchMap((loginResponse) =>
        this.http.get<User>(`${environment.apiUrl}/users/${loginResponse.userId}`).pipe(
          tap((user) => {
            const currentUser: CurrentUser = {
              userId: loginResponse.userId,
              companyId: loginResponse.companyId,
              role: loginResponse.role,
              firstName: user.firstName,
              lastName: user.lastName,
            };
            localStorage.setItem(STORAGE_KEY, JSON.stringify(currentUser));
            this.currentUser.set(currentUser);
            this.isAuthenticated.set(true);
          }),
          switchMap(() => [this.currentUser()!])
        )
      )
    );
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
  }

  private readFromStorage(): CurrentUser | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  updateCurrentUserName(firstName: string, lastName: string): void {
  const current = this.currentUser();
  if (!current) return;

  const updated: CurrentUser = { ...current, firstName, lastName };
  localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
  this.currentUser.set(updated);
}
}
