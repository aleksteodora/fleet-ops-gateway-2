import { Component, inject, signal, computed } from '@angular/core';
import { catchError, finalize, of, tap } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { CompanyService } from '../../../core/services/company.service';
import { User } from '../../../core/models/user.model';
import { Company } from '../../../core/models/company.model';
import { RouterLink } from '@angular/router';

type SortField = 'firstName' | 'lastName' | 'role';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-users-list',
  imports: [RouterLink],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss',
})
export class UsersListComponent {
  private readonly userService = inject(UserService);
  private readonly companyService = inject(CompanyService);

  readonly users = signal<User[]>([]);
  readonly companies = signal<Company[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly currentPage = signal(0);
  readonly totalPages = signal(0);
  readonly pageSize = 20;

  readonly sortField = signal<SortField>('firstName');
  readonly sortDirection = signal<SortDirection>('asc');
  readonly selectedCompanyId = signal<number | null>(null);

  constructor() {
    this.loadCompanies();
    this.loadUsers();
  }

  loadCompanies(): void {
    this.companyService
      .getCompanies({ size: 100 })
      .pipe(
        tap((response) => this.companies.set(response.content)),
        catchError(() => {
          return of(null);
        })
      )
      .subscribe();
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.userService
      .getUsers({
        page: this.currentPage(),
        size: this.pageSize,
        sortBy: this.sortField(),
        sortDirection: this.sortDirection(),
        companyId: this.selectedCompanyId() ?? undefined,
      })
      .pipe(
        tap((response) => {
          this.users.set(response.content);
          this.totalPages.set(response.totalPages);
        }),
        catchError(() => {
          this.errorMessage.set('Failed to load users. Please try again.');
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
    this.loadUsers();
  }

  onCompanyFilterChange(companyId: string): void {
    this.selectedCompanyId.set(companyId ? Number(companyId) : null);
    this.currentPage.set(0);
    this.loadUsers();
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadUsers();
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