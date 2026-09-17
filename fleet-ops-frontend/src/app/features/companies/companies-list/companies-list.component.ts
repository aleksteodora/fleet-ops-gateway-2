import { Component, inject, signal } from '@angular/core';
import { catchError, finalize, of, tap } from 'rxjs';
import { RouterLink } from '@angular/router';
import { CompanyService } from '../../../core/services/company.service';
import { Company } from '../../../core/models/company.model';
import { DatePipe } from '@angular/common';

type SortField = 'name' | 'createdAt';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-companies-list',
  imports: [RouterLink,  DatePipe],
  templateUrl: './companies-list.component.html',
  styleUrl: './companies-list.component.scss',
})
export class CompaniesListComponent {
  private readonly companyService = inject(CompanyService);

  readonly companies = signal<Company[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly currentPage = signal(0);
  readonly totalPages = signal(0);
  readonly pageSize = 20;

  readonly sortField = signal<SortField>('name');
  readonly sortDirection = signal<SortDirection>('asc');

  constructor() {
    this.loadCompanies();
  }

  loadCompanies(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.companyService
      .getCompanies({
        page: this.currentPage(),
        size: this.pageSize,
        sortBy: this.sortField(),
        sortDirection: this.sortDirection(),
      })
      .pipe(
        tap((response) => {
          this.companies.set(response.content);
          this.totalPages.set(response.totalPages);
        }),
        catchError(() => {
          this.errorMessage.set('Failed to load companies. Please try again.');
          return of(null);
        }),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe();
  }

  onSortChange(field: SortField): void {
    if (this.sortField() === field) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }
    this.currentPage.set(0);
    this.loadCompanies();
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadCompanies();
  }

  onPreviousPage(): void {
    if (this.currentPage() > 0) {
      this.onPageChange(this.currentPage() - 1);
    }
  }

  onNextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.onPageChange(this.currentPage() + 1);
    }
  }
}