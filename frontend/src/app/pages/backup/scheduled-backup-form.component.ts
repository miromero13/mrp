import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output } from '@angular/core';
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

  protected readonly frequencyOptions = [
    { label: 'Diariamente', value: 'daily' },
    { label: 'Semanalmente', value: 'weekly' },
    { label: 'Mensualmente', value: 'monthly' }
  ];

  protected readonly daysOfWeek = [
    { label: 'Lunes', value: 'MON' },
    { label: 'Martes', value: 'TUE' },
    { label: 'Miércoles', value: 'WED' },
    { label: 'Jueves', value: 'THU' },
    { label: 'Viernes', value: 'FRI' },
    { label: 'Sábado', value: 'SAT' },
    { label: 'Domingo', value: 'SUN' }
  ];

  protected readonly hours = Array.from({ length: 24 }, (_, i) => i);
  protected readonly minutes = Array.from({ length: 60 }, (_, i) => i);

  protected readonly form = this.formBuilder.nonNullable.group({
    frequency: ['daily', Validators.required],
    hour: ['0', Validators.required],
    minute: ['0', Validators.required],
    daysOfWeek: [['MON']],
    cronExpression: [''],
    userId: [''],
    enterpriseId: ['']
  });

  protected readonly showDaysOfWeek = computed(() => this.form.get('frequency')?.value === 'weekly');

  constructor() {
    effect(() => {
      const user = this.currentUser();
      if (user) {
        this.form.patchValue({
          userId: user.id ?? '',
          enterpriseId: user.enterprise?.id ?? '',
        });
      }
    });

    this.form.valueChanges.subscribe((value) => {
      const frequency = value.frequency ?? 'daily';
      const hour = value.hour ?? '0';
      const minute = value.minute ?? '0';
      const daysOfWeek = Array.isArray(value.daysOfWeek) && value.daysOfWeek.length ? value.daysOfWeek : ['MON'];

      const cronExpression = this.generateCronExpression(frequency, hour, minute, daysOfWeek);
      this.form.get('cronExpression')?.setValue(cronExpression, { emitEvent: false });
    });
  }

  protected toggleDay(dayValue: string): void {
    const daysControl = this.form.get('daysOfWeek');
    if (!daysControl) return;

    const currentDays = (daysControl.value || []);
    const index = currentDays.indexOf(dayValue);

    if (index > -1) {
      currentDays.splice(index, 1);
    } else {
      currentDays.push(dayValue);
    }

    daysControl.setValue([...currentDays]);
  }

  protected isDaySelected(dayValue: string): boolean {
    const daysControl = this.form.get('daysOfWeek');
    return (daysControl?.value || []).includes(dayValue);
  }

  private generateCronExpression(
    frequency: string,
    hour: string | number,
    minute: string | number,
    daysOfWeek?: string[]
  ): string {

    const h = String(hour).padStart(2, '0');
    const m = String(minute).padStart(2, '0');

    switch (frequency) {

      case 'daily':
        return `0 ${m} ${h} * * ?`;

      case 'weekly':
        return `0 ${m} ${h} ? * ${(daysOfWeek || ['MON']).join(',')}`;

      case 'monthly':
        return `0 ${m} ${h} 1 * ?`;

      default:
        return `0 ${m} ${h} * * ?`;
    }
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const scheduleValue = this.form.getRawValue();
    this.submitted.emit({
      schedulePayload: {
        cronExpression: scheduleValue.cronExpression,
      },
      createPayload: {
        userId: scheduleValue.userId,
        enterpriseId: scheduleValue.enterpriseId,
      }
    });
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }
}
