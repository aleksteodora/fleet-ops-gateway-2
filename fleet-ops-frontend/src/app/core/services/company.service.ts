import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Company } from '../models/company.model';
import { PagedResponse } from '../models/paged-response.model';

export interface CompanyQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

@Injectable({ providedIn: 'root' })
export class CompanyService {
  private readonly http = inject(HttpClient);

  getCompanies(params: CompanyQueryParams = {}): Observable<PagedResponse<Company>> {
    let httpParams = new HttpParams()
      .set('page', params.page ?? 0)
      .set('size', params.size ?? 20);

    if (params.sortBy) {
      httpParams = httpParams.set('sort', `${params.sortBy},${params.sortDirection ?? 'asc'}`);
    }

    return this.http.get<PagedResponse<Company>>(`${environment.apiUrl}/companies`, { params: httpParams });
  }
}