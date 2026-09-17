import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Company } from '../models/company.model';
import { PagedResponse } from '../models/paged-response.model';

@Injectable({ providedIn: 'root' })
export class CompanyService {
  private readonly http = inject(HttpClient);

  getCompanies(size = 100): Observable<PagedResponse<Company>> {
    const params = new HttpParams().set('page', 0).set('size', size);
    return this.http.get<PagedResponse<Company>>(`${environment.apiUrl}/companies`, { params });
  }
}