import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';
import { PagedResponse } from '../models/paged-response.model';
import { CreateUserRequest } from '../models/create-user-request.model';
import { UpdateUserRequest } from '../models/update-user-request.model';

export interface UserQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
  companyId?: number;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  getUsers(params: UserQueryParams): Observable<PagedResponse<User>> {
    let httpParams = new HttpParams()
      .set('page', params.page ?? 0)
      .set('size', params.size ?? 15);

    if (params.sortBy) {
      httpParams = httpParams.set('sort', `${params.sortBy},${params.sortDirection ?? 'asc'}`);
    }

    const url = params.companyId
      ? `${environment.apiUrl}/companies/${params.companyId}/users`
      : `${environment.apiUrl}/users`;

    return this.http.get<PagedResponse<User>>(url, { params: httpParams });
  }

createUser(request: CreateUserRequest): Observable<User> {
  return this.http.post<User>(`${environment.apiUrl}/users`, request);
}

updateUser(id: number, request: UpdateUserRequest): Observable<User> {
  return this.http.put<User>(`${environment.apiUrl}/users/${id}`, request);
}

deactivateUser(id: number): Observable<void> {
  return this.http.patch<void>(`${environment.apiUrl}/users/${id}`, null);
}

getUserById(id: number): Observable<User> {
  return this.http.get<User>(`${environment.apiUrl}/users/${id}`);
}
}