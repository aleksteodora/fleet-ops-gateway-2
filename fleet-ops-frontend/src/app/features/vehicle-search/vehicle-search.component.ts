import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, finalize, of, tap } from 'rxjs';
import { LucideSearch } from '@lucide/angular';
import { VehicleSearchService } from '../../core/services/vehicle-search.service';
import { VehicleSearchResult } from '../../core/models/vehicle-search-result.model';

@Component({
  selector: 'app-vehicle-search',
  imports: [ReactiveFormsModule, LucideSearch],
  templateUrl: './vehicle-search.component.html',
  styleUrl: './vehicle-search.component.scss',
})
export class VehicleSearchComponent {
  private readonly fb = inject(FormBuilder);
  private readonly vehicleSearchService = inject(VehicleSearchService);

  readonly isSearching = signal(false);
  readonly result = signal<VehicleSearchResult | null>(null);
  readonly errorMessage = signal<string | null>(null);

  readonly searchForm = this.fb.group({
    vin: ['', [Validators.required]],
  });

  onSearch(): void {
    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      return;
    }

    const vin = this.searchForm.getRawValue().vin!;

    this.isSearching.set(true);
    this.errorMessage.set(null);
    this.result.set(null);

    this.vehicleSearchService
      .search(vin)
      .pipe(
        tap((response) => this.result.set(response)),
        catchError(() => {
          this.errorMessage.set('Something went wrong. Please try again.');
          return of(null);
        }),
        finalize(() => this.isSearching.set(false))
      )
      .subscribe();
  }

  onClear(): void {
    this.searchForm.reset();
    this.result.set(null);
    this.errorMessage.set(null);
  }
}