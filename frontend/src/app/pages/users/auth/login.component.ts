import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { provideIcons } from '@ng-icons/core';
import { lucideCog } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCardImports } from '@spartan-ng/helm/card';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../../core/users/services/auth.service';
import { APP_NAME } from '../../../core/config/app.config';
import { APP_ROUTE_URLS } from '../../../core/config/app-routes.utils';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, ...HlmCardImports, ...HlmInputImports, ...HlmLabelImports, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucideCog })],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly appName = APP_NAME;
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const { email, password } = this.loginForm.getRawValue();
    this.authService
      .login({ email, password })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => this.router.navigateByUrl(APP_ROUTE_URLS.dashboard),
        error: (error: Error) => this.errorMessage.set(error.message),
      });
  }

  get emailControl() {
    return this.loginForm.controls.email;
  }

  get passwordControl() {
    return this.loginForm.controls.password;
  }
}
