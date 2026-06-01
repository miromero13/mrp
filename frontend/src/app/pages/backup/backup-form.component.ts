import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { BackupFormValue } from '../../core/backups/models/backup.models';
import { AuthService } from '../../core/users/services/auth.service';

@Component({
  selector: 'app-backup-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ...HlmButtonImports,
    ...HlmInputImports,
    ...HlmLabelImports,
    ...HlmSelectImports
  ],
  templateUrl: './backup-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BackupFormComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  readonly submitLabel = input('Guardar Backup');
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<BackupFormValue>();

  protected readonly currentUser = this.authService.currentUser;
  protected readonly userName = computed(() => this.currentUser()?.name ?? '');
  protected readonly enterpriseName = computed(() => this.currentUser()?.enterprise?.name ?? '');
  protected readonly userId = computed(() => this.currentUser()?.id ?? '');
  protected readonly enterpriseId = computed(() => this.currentUser()?.enterprise?.id ?? '');

  protected readonly form = this.formBuilder.nonNullable.group({
    userId: [''],
    enterpriseId: ['']
  });

  constructor() {
    effect(() => {
      const user = this.currentUser();
      if (user) {
        this.form.patchValue({
          userId: user.id ?? '',
          enterpriseId: user.enterprise?.id ?? '',
        });
        console.log('userId:', user.id);
        console.log('enterpriseId:', user.enterprise?.id);
      }
    });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted.emit(this.form.getRawValue());
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }
}
