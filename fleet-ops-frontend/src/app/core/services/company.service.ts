import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Company } from '../models/company.model';
import { PagedResponse } from '../models/paged-response.model';
import { CreateCompanyRequest } from '../models/create-company-request.model';
import { UpdateCompanyRequest } from '../models/update-company-request.model';

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
      .set('size', params.size ?? 15);

    if (params.sortBy) {
      httpParams = httpParams.set('sort', `${params.sortBy},${params.sortDirection ?? 'asc'}`);
    }

    return this.http.get<PagedResponse<Company>>(`${environment.apiUrl}/companies`, { params: httpParams });
  }

  createCompany(request: CreateCompanyRequest): Observable<Company> {
    return this.http.post<Company>(`${environment.apiUrl}/companies`, request);
  }

  updateCompany(id: number, request: UpdateCompanyRequest): Observable<Company> {
    return this.http.put<Company>(`${environment.apiUrl}/companies/${id}`, request);
  }

  deactivateCompany(id: number): Observable<void> {
    return this.http.patch<void>(`${environment.apiUrl}/companies/${id}`, null);
  }
}