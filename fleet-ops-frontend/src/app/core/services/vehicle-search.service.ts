import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { VehicleSearchResult } from '../models/vehicle-search-result.model';

@Injectable({ providedIn: 'root' })
export class VehicleSearchService {
  private readonly http = inject(HttpClient);

  search(vin: string): Observable<VehicleSearchResult> {
    const params = new HttpParams().set('vin', vin);
    return this.http.get<VehicleSearchResult>(`${environment.apiUrl}/vehicle-search`, { params });
  }
}