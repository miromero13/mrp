import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CreateWorkShiftFormValue, WorkShiftListItem } from '../../../core/workshifts/models/workshift.models';

@Component({
  selector: 'app-workshift-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ...HlmButtonImports,
    ...HlmInputImports,
    ...HlmLabelImports,
    ...HlmSelectImports,
  ],
  templateUrl: './workshift-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkShiftFormComponent {
  private readonly formBuilder = inject(FormBuilder);

  readonly submitLabel = input('Guardar Turno');
  readonly mode = input<'create' | 'edit' | 'view'>('create');
  readonly initialWorkShift = input<WorkShiftListItem | null>(null);
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<CreateWorkShiftFormValue>();

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    startDate: ['', [Validators.required]],
    endDate: ['', [Validators.required]],
  });

  protected readonly isReadOnly = computed(() => this.mode() === 'view');

  constructor() {
    effect(() => {
      const workShift = this.initialWorkShift();
      this.form.reset({
        name: workShift?.name ?? '',
        startDate: workShift?.startdate ? this.toLocalDateTimeValue(workShift.startdate) : '',
        endDate: workShift?.enddate ? this.toLocalDateTimeValue(workShift.enddate) : '',
      });
    });
  }

  protected submit(): void {
    if (this.isReadOnly()) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.submitted.emit({
      name: value.name.trim(),
      startdate: new Date(value.startDate),
      enddate: new Date(value.endDate),
    });
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }

  private toLocalDateTimeValue(value: Date): string {
    const date = new Date(value);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }
}
