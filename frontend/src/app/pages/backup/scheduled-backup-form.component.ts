import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { BackupFormValue, ScheduleBackupFormValue } from '../../core/backups/models/backup.models';
import { AuthService } from '../../core/users/services/auth.service';

export interface ScheduledBackupSubmitPayload {
  schedulePayload: ScheduleBackupFormValue;
  createPayload: BackupFormValue;
}

@Component({
  selector: 'app-scheduled-backup-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...HlmButtonImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  templateUrl: './scheduled-backup-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScheduledBackupFormComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  readonly submitLabel = input('Programar Backup');
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<ScheduledBackupSubmitPayload>();

  protected readonly currentUser = this.authService.currentUser;
  protected readonly userName = computed(() => this.currentUser()?.name ?? '');
  protected readonly enterpriseName = computed(() => this.currentUser()?.enterprise?.name ?? '');
  protected readonly userId = computed(() => this.currentUser()?.id ?? '');
  protected readonly enterpriseId = computed(() => this.currentUser()?.enterprise?.id ?? '');

  protected readonly form = this.formBuilder.nonNullable.group({
    cronExpression: ['', [Validators.required, Validators.minLength(5)]]
  });

  protected submit(): void {
    if (this.form.invalid || !this.userId() || !this.enterpriseId()) {
      this.form.markAllAsTouched();
      return;
    }

    const scheduleValue = this.form.getRawValue();
    
    this.submitted.emit({
      schedulePayload: {
        cronExpression: scheduleValue.cronExpression,
      },
      createPayload: {
        userId: this.userId(),
        enterpriseId: this.enterpriseId(),
      }
    });
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }
}
