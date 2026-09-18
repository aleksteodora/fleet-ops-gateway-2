import { Component, inject, signal } from '@angular/core';
import { catchError, finalize, of, tap } from 'rxjs';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CompanyService } from '../../../core/services/company.service';
import { Company } from '../../../core/models/company.model';

type SortField = 'name' | 'createdAt';
type SortDirection = 'asc' | 'desc';
type FormMode = 'add' | 'edit';

@Component({
  selector: 'app-companies-list',
  imports: [RouterLink, DatePipe, ReactiveFormsModule],
  templateUrl: './companies-list.component.html',
  styleUrl: './companies-list.component.scss',
})
export class CompaniesListComponent {
  private readonly companyService = inject(CompanyService);
  private readonly fb = inject(FormBuilder);

  readonly companies = signal<Company[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly currentPage = signal(0);
  readonly totalPages = signal(0);
  readonly pageSize = 15;

  readonly sortField = signal<SortField>('name');
  readonly sortDirection = signal<SortDirection>('asc');

  readonly formMode = signal<FormMode>('add');
  readonly editingCompanyId = signal<number | null>(null);
  readonly formError = signal<string | null>(null);
  readonly isSubmittingForm = signal(false);

  readonly companyToDeactivate = signal<Company | null>(null);
  readonly isDeactivating = signal(false);

  readonly companyForm = this.fb.group({
    name: ['', [Validators.required]],
  });

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

  startEdit(company: Company): void {
    this.formMode.set('edit');
    this.editingCompanyId.set(company.id);
    this.formError.set(null);
    this.companyForm.setValue({ name: company.name });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.formMode.set('add');
    this.editingCompanyId.set(null);
    this.formError.set(null);
    this.companyForm.reset({ name: '' });
  }

  onSubmitForm(): void {
    if (this.companyForm.invalid) {
      this.companyForm.markAllAsTouched();
      return;
    }

    this.formError.set(null);
    this.isSubmittingForm.set(true);

    const { name } = this.companyForm.getRawValue();

    const request$ =
      this.formMode() === 'edit'
        ? this.companyService.updateCompany(this.editingCompanyId()!, { name: name! })
        : this.companyService.createCompany({ name: name! });

    request$
      .pipe(
        tap(() => {
          this.resetForm();
          this.loadCompanies();
        }),
        catchError((error) => {
          this.formError.set(this.mapErrorMessage(error));
          return of(null);
        }),
        finalize(() => this.isSubmittingForm.set(false))
      )
      .subscribe();
  }

  private mapErrorMessage(error: any): string {
    if (error.status === 409) {
      return 'A company with this name already exists.';
    }
    return this.formMode() === 'edit'
      ? 'Failed to update company. Please try again.'
      : 'Failed to add company. Please try again.';
  }

  confirmDeactivate(company: Company): void {
    this.companyToDeactivate.set(company);
  }

  cancelDeactivate(): void {
    this.companyToDeactivate.set(null);
  }

  deactivateCompany(): void {
    const company = this.companyToDeactivate();
    if (!company) return;

    this.isDeactivating.set(true);

    this.companyService
      .deactivateCompany(company.id)
      .pipe(
        tap(() => {
          this.companyToDeactivate.set(null);
          this.loadCompanies();
        }),
        catchError(() => of(null)),
        finalize(() => this.isDeactivating.set(false))
      )
      .subscribe();
  }
}