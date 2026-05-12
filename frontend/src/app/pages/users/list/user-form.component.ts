import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { AuthService } from '../../../core/users/services/auth.service';
import { CreateUserFormValue } from '../../../core/users/models/user.models';
import { RoleListItem } from '../../../core/users/models/role.models';
import { WorkShiftListItem } from '../../../core/workshifts/models/workshift.models';
import { WorkShiftService } from '../../../core/workshifts/services/workshift.service';
import { finalize, take } from 'rxjs/operators';

const DAY_OPTIONS = [
  { value: 'MONDAY', label: 'Lunes' },
  { value: 'TUESDAY', label: 'Martes' },
  { value: 'WEDNESDAY', label: 'Miércoles' },
  { value: 'THURSDAY', label: 'Jueves' },
  { value: 'FRIDAY', label: 'Viernes' },
  { value: 'SATURDAY', label: 'Sábado' },
  { value: 'SUNDAY', label: 'Domingo' },
] as const;

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...HlmButtonImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  templateUrl: './user-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly workShiftService = inject(WorkShiftService);
  private readonly authService = inject(AuthService);

  readonly availableRoles = input.required<ReadonlyArray<RoleListItem>>();
  readonly submitLabel = input('Guardar usuario');
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<CreateUserFormValue>();

  protected readonly availableWorkShifts = signal<ReadonlyArray<WorkShiftListItem>>([]);
  protected readonly loadingWorkShifts = signal(false);
  protected readonly selectedRoleId = signal('');

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
    workShiftAssignments: this.formBuilder.array([]),
  });

  protected readonly hasRoleError = computed(() => this.form.controls.roleId.touched && this.form.controls.roleId.invalid);
  protected readonly selectedRole = computed(() =>
    this.availableRoles().find((role) => role.id === this.selectedRoleId()) ?? null,
  );
  protected readonly isEmployeeRoleSelected = computed(() => this.selectedRole()?.name === 'employee');
  protected readonly isSuperadmin = computed(() => this.authService.currentUser()?.role?.name === 'superadmin');
  protected readonly dayOptions = DAY_OPTIONS;

  get assignments(): FormArray {
    return this.form.controls.workShiftAssignments;
  }

  ngOnInit(): void {
    if (!this.isSuperadmin()) {
      this.loadWorkShifts();
    }
  }

  protected onRoleChange(roleId: string | null): void {
    this.selectedRoleId.set(roleId ?? '');
    this.form.controls.roleId.setValue(roleId ?? '');
    this.form.controls.roleId.markAsTouched();

    if (this.isEmployeeRoleSelected()) {
      if (this.assignments.length === 0) {
        this.addAssignment();
      }
      return;
    }

    this.assignments.clear();
  }

  protected onGenderChange(gender: string | null): void {
    this.form.controls.gender.setValue(gender ?? '');
  }

  protected addAssignment(): void {
    this.assignments.push(
      this.formBuilder.nonNullable.group({
        dayOfWeek: ['MONDAY', [Validators.required]],
        workShiftId: ['', [Validators.required]],
      }),
    );
  }

  protected removeAssignment(index: number): void {
    if (this.assignments.length > 1) {
      this.assignments.removeAt(index);
      return;
    }

    this.assignments.at(0)?.reset({ dayOfWeek: 'MONDAY', workShiftId: '' });
  }

  protected trackAssignment = (index: number): number => index;

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.isEmployeeRoleSelected() && this.assignments.length === 0) {
      this.addAssignment();
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const workShiftAssignments = value.workShiftAssignments as Array<{ dayOfWeek: string; workShiftId: string }>;
    this.submitted.emit({
      name: value.name.trim(),
      email: value.email.trim(),
      password: value.password,
      roleId: value.roleId,
      phone: value.phone.trim() || null,
      address: value.address.trim() || null,
      gender: (value.gender || null) as 'masculino' | 'femenino' | 'otro' | null,
      workShiftAssignments: workShiftAssignments.map((assignment) => ({
        dayOfWeek: assignment.dayOfWeek,
        workShiftId: assignment.workShiftId,
      })),
    });
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }

  protected isSelectedRoleEmployee(roleId: string): boolean {
    return this.availableRoles().find((role) => role.id === roleId)?.name === 'employee';
  }

  protected onAssignmentDayChange(index: number, dayOfWeek: string | null): void {
    this.assignments.at(index)?.get('dayOfWeek')?.setValue(dayOfWeek ?? 'MONDAY');
  }

  protected onAssignmentShiftChange(index: number, workShiftId: string | null): void {
    this.assignments.at(index)?.get('workShiftId')?.setValue(workShiftId ?? '');
  }

  private loadWorkShifts(): void {
    this.loadingWorkShifts.set(true);

    this.workShiftService
      .listWorkShifts()
      .pipe(
        take(1),
        finalize(() => this.loadingWorkShifts.set(false)),
      )
      .subscribe({
        next: (workShifts) => this.availableWorkShifts.set(workShifts),
        error: () => this.availableWorkShifts.set([]),
      });
  }
}
