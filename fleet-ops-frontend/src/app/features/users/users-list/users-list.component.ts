import { Component, inject, signal } from '@angular/core';
import { catchError, finalize, of, tap } from 'rxjs';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { CompanyService } from '../../../core/services/company.service';
import { User } from '../../../core/models/user.model';
import { Company } from '../../../core/models/company.model';

type SortField = 'firstName' | 'lastName' | 'role';
type SortDirection = 'asc' | 'desc';
type FormMode = 'add' | 'edit';

@Component({
  selector: 'app-users-list',
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss',
})
export class UsersListComponent {
  private readonly userService = inject(UserService);
  private readonly companyService = inject(CompanyService);
  private readonly fb = inject(FormBuilder);

  readonly users = signal<User[]>([]);
  readonly companies = signal<Company[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly currentPage = signal(0);
  readonly totalPages = signal(0);
  readonly pageSize = 15;

  readonly sortField = signal<SortField>('firstName');
  readonly sortDirection = signal<SortDirection>('asc');
  readonly selectedCompanyId = signal<number | null>(null);

  readonly formMode = signal<FormMode>('add');
  readonly editingUserId = signal<number | null>(null);
  readonly formError = signal<string | null>(null);
  readonly isSubmittingForm = signal(false);

  readonly userToDeactivate = signal<User | null>(null);
  readonly isDeactivating = signal(false);

  readonly addUserForm = this.fb.group({
    email: ['', [Validators.required]],
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    role: ['COMPANY_USER' as 'ADMIN' | 'COMPANY_USER', [Validators.required]],
    companyId: [null as number | null],
  });

  readonly editUserForm = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
  });

  constructor() {
    this.loadCompanies();
    this.loadUsers();
  }

  loadCompanies(): void {
    this.companyService
      .getCompanies({ size: 100 })
      .pipe(
        tap((response) => this.companies.set(response.content)),
        catchError(() => of(null))
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

  startEdit(user: User): void {
    this.formMode.set('edit');
    this.editingUserId.set(user.id);
    this.formError.set(null);
    this.editUserForm.setValue({
      firstName: user.firstName,
      lastName: user.lastName,
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.formMode.set('add');
    this.editingUserId.set(null);
    this.formError.set(null);
    this.addUserForm.reset({
      email: '',
      firstName: '',
      lastName: '',
      role: 'COMPANY_USER',
      companyId: null,
    });
    this.editUserForm.reset({ firstName: '', lastName: '' });
  }

  onSubmitForm(): void {
    if (this.formMode() === 'edit') {
      this.submitEdit();
    } else {
      this.submitAdd();
    }
  }

  private submitEdit(): void {
    if (this.editUserForm.invalid) {
      this.editUserForm.markAllAsTouched();
      return;
    }

    this.formError.set(null);
    this.isSubmittingForm.set(true);

    const { firstName, lastName } = this.editUserForm.getRawValue();

    this.userService
      .updateUser(this.editingUserId()!, { firstName: firstName!, lastName: lastName! })
      .pipe(
        tap(() => {
          this.resetForm();
          this.loadUsers();
        }),
        catchError(() => {
          this.formError.set('Failed to update user. Please try again.');
          return of(null);
        }),
        finalize(() => this.isSubmittingForm.set(false))
      )
      .subscribe();
  }

  private submitAdd(): void {
    if (this.addUserForm.invalid) {
      this.addUserForm.markAllAsTouched();
      return;
    }

    this.formError.set(null);
    this.isSubmittingForm.set(true);

    const { email, firstName, lastName, role, companyId } = this.addUserForm.getRawValue();

    this.userService
      .createUser({
        email: email!,
        firstName: firstName!,
        lastName: lastName!,
        role: role!,
        companyId: role === 'ADMIN' ? null : companyId,
      })
      .pipe(
        tap(() => {
          this.resetForm();
          this.loadUsers();
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
      return 'A user with this email already exists.';
    }
    if (error.status === 400) {
      return error.error?.detail ?? 'Invalid user data.';
    }
    if (error.status === 404) {
      return 'Selected company was not found.';
    }
    return 'Failed to create user. Please try again.';
  }

  confirmDeactivate(user: User): void {
    this.userToDeactivate.set(user);
  }

  cancelDeactivate(): void {
    this.userToDeactivate.set(null);
  }

  deactivateUser(): void {
    const user = this.userToDeactivate();
    if (!user) return;

    this.isDeactivating.set(true);

    this.userService
      .deactivateUser(user.id)
      .pipe(
        tap(() => {
          this.userToDeactivate.set(null);
          this.loadUsers();
        }),
        catchError(() => of(null)),
        finalize(() => this.isDeactivating.set(false))
      )
      .subscribe();
  }
}