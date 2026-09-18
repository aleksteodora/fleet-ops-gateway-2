import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';
import { PagedResponse } from '../models/paged-response.model';

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
}