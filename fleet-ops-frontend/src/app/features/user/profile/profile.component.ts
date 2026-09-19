import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, finalize, of, tap } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly fb = inject(FormBuilder);

  readonly profile = signal<User | null>(null);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly isEditing = signal(false);
  readonly formError = signal<string | null>(null);
  readonly isSubmitting = signal(false);
  readonly successMessage = signal<string | null>(null);

  readonly editForm = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
  });

  constructor() {
    this.loadProfile();
  }

  loadProfile(): void {
    const userId = this.authService.currentUser()?.userId;
    if (!userId) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.userService
      .getUserById(userId)
      .pipe(
        tap((user) => this.profile.set(user)),
        catchError(() => {
          this.errorMessage.set('Failed to load profile. Please try again.');
          return of(null);
        }),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe();
  }

  startEdit(): void {
    const profile = this.profile();
    if (!profile) return;

    this.isEditing.set(true);
    this.formError.set(null);
    this.successMessage.set(null);
    this.editForm.setValue({
      firstName: profile.firstName,
      lastName: profile.lastName,
    });
  }

  cancelEdit(): void {
    this.isEditing.set(false);
    this.formError.set(null);
  }

  onSubmit(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    const userId = this.authService.currentUser()?.userId;
    if (!userId) return;

    this.formError.set(null);
    this.isSubmitting.set(true);

    const { firstName, lastName } = this.editForm.getRawValue();

    this.userService
      .updateUser(userId, { firstName: firstName!, lastName: lastName! })
      .pipe(
        tap((updatedUser) => {
          this.profile.set(updatedUser);
          this.authService.updateCurrentUserName(updatedUser.firstName, updatedUser.lastName);
          this.isEditing.set(false);
          this.successMessage.set('Profile updated successfully.');
        }),
        catchError(() => {
          this.formError.set('Failed to update profile. Please try again.');
          return of(null);
        }),
        finalize(() => this.isSubmitting.set(false))
      )
      .subscribe();
  }
}