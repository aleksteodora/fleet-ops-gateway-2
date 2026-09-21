import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProviderStatistics, OutcomeStatistics, VolumeStatistics, UserVolumeStatistics } from '../models/statistics.model';

export interface DateRangeParams {
  from?: string;
  to?: string;
}

@Injectable({ providedIn: 'root' })
export class StatisticsService {
  private readonly http = inject(HttpClient);

  getProviderStatistics(range: DateRangeParams): Observable<ProviderStatistics> {
    return this.http.get<ProviderStatistics>(
      `${environment.apiUrl}/statistics/providers`,
      { params: this.buildParams(range) }
    );
  }

  getOutcomeStatistics(range: DateRangeParams): Observable<OutcomeStatistics> {
    return this.http.get<OutcomeStatistics>(
      `${environment.apiUrl}/statistics/outcomes`,
      { params: this.buildParams(range) }
    );
  }

  getVolumeStatistics(range: DateRangeParams): Observable<VolumeStatistics> {
    return this.http.get<VolumeStatistics>(
      `${environment.apiUrl}/statistics/volume`,
      { params: this.buildParams(range) }
    );
  }

  getMySearchStatistics(range: DateRangeParams): Observable<UserVolumeStatistics> {
    return this.http.get<UserVolumeStatistics>(
      `${environment.apiUrl}/statistics/my-searches`,
      { params: this.buildParams(range) }
    );
  }

  private buildParams(range: DateRangeParams): HttpParams {
    let params = new HttpParams();
    if (range.from) {
      params = params.set('from', range.from);
    }
    if (range.to) {
      params = params.set('to', range.to);
    }
    return params;
  }
}