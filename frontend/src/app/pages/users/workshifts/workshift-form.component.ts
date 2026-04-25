import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CreateWorkShiftFormValue } from '../../../core/workshifts/models/workshift.models';

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
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<CreateWorkShiftFormValue>();

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    startDate: ['', [Validators.required]],
    endDate: ['', [Validators.required]],
  });

  protected submit(): void {
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
}
