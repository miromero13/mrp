import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CreateEnterpriseFormValue } from '../../../core/enterprises/models/enterprise.models';

@Component({
  selector: 'app-enterprise-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...HlmButtonImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  templateUrl: './enterprise-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnterpriseFormComponent {
  private readonly formBuilder = inject(FormBuilder);

  readonly submitLabel = input('Guardar empresa');
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<CreateEnterpriseFormValue>();

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    nit: ['', [Validators.required, Validators.maxLength(20)]],
    address: ['', [Validators.maxLength(200)]],
    adminName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    adminEmail: ['', [Validators.required, Validators.email]],
    adminPassword: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(12),
        Validators.pattern('^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$'),
      ],
    ],
    adminPhone: ['', [Validators.maxLength(20), Validators.pattern('^$|^\\+?[0-9. ()-]{7,25}$')]],
    adminAddress: ['', [Validators.maxLength(200)]],
    adminGender: [''],
  });

  protected onGenderChange(gender: string | null): void {
    this.form.controls.adminGender.setValue(gender ?? '');
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.submitted.emit({
      name: value.name.trim(),
      nit: value.nit.trim(),
      address: value.address.trim() || null,
      adminName: value.adminName.trim(),
      adminEmail: value.adminEmail.trim(),
      adminPassword: value.adminPassword,
      adminPhone: value.adminPhone.trim() || null,
      adminAddress: value.adminAddress.trim() || null,
      adminGender: (value.adminGender || null) as 'masculino' | 'femenino' | 'otro' | null,
    });
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }
}
