import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { catchError, of, tap } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly errorMessage = signal<string | null>(null);
  readonly isSubmitting = signal(false);
  readonly showPassword = signal(false);

  readonly loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null);
    this.isSubmitting.set(true);

    const { email, password } = this.loginForm.getRawValue();

    this.authService
      .login({ email: email!, password: password! })
      .pipe(
        tap((currentUser) => {
          this.isSubmitting.set(false);
          const destination = currentUser.role === 'ADMIN' ? '/admin' : '/user';
          this.router.navigate([destination]);
        }),
        catchError(() => {
          this.isSubmitting.set(false);
          this.errorMessage.set('Invalid email or password.');
          return of(null);
        })
      )
      .subscribe();
  }

  togglePasswordVisibility(): void {
    this.showPassword.update((value) => !value);
  }
}