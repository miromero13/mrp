import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { RoleListItem } from '../../../core/users/models/role.models';
import { CreateUserFormValue } from '../../../core/users/models/user.models';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ...HlmButtonImports,
    ...HlmInputImports,
    ...HlmLabelImports,
    ...HlmSelectImports,
  ],
  templateUrl: './user-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserFormComponent {
  private readonly formBuilder = inject(FormBuilder);

  readonly availableRoles = input.required<ReadonlyArray<RoleListItem>>();
  readonly submitLabel = input('Guardar usuario');
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<CreateUserFormValue>();

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(12),
        Validators.pattern('^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$'),
      ],
    ],
    phone: ['', [Validators.maxLength(20), Validators.pattern('^$|^\\+?[0-9. ()-]{7,25}$')]],
    gender: [''],
    address: ['', [Validators.maxLength(200)]],
    roleId: ['', [Validators.required]],
  });

  protected readonly hasRoleError = computed(
    () => this.form.controls.roleId.touched && this.form.controls.roleId.invalid,
  );

  protected onRoleChange(roleId: string | null): void {
    this.form.controls.roleId.setValue(roleId ?? '');
    this.form.controls.roleId.markAsTouched();
  }

  protected onGenderChange(gender: string | null): void {
    this.form.controls.gender.setValue(gender ?? '');
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.submitted.emit({
      name: value.name.trim(),
      email: value.email.trim(),
      password: value.password,
      roleId: value.roleId,
      phone: value.phone.trim() || null,
      address: value.address.trim() || null,
      gender: (value.gender || null) as 'masculino' | 'femenino' | 'otro' | null,
    });
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }
}
