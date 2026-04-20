import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { ReactiveFormsModule, Validators, FormBuilder } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSwitchImports } from '@spartan-ng/helm/switch';
import { PermissionListItem } from '../../../core/users/models/permission.models';
import { RoleFormValue } from '../../../core/users/models/role.models';

type PermissionGroupKey = 'roles' | 'permisos' | 'usuarios' | 'material' | 'other';

interface PermissionGroup {
  key: PermissionGroupKey;
  label: string;
  permissions: PermissionListItem[];
}

@Component({
  selector: 'app-role-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...HlmButtonImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSwitchImports],
  templateUrl: './role-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RoleFormComponent {
  private readonly formBuilder = inject(FormBuilder);

  readonly availablePermissions = input.required<ReadonlyArray<PermissionListItem>>();
  readonly initialName = input('');
  readonly initialPermissionIds = input<ReadonlyArray<string>>([]);
  readonly submitLabel = input('Guardar rol');
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<RoleFormValue>();

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
  });

  protected readonly selectedPermissionIds = signal<string[]>([]);
  protected readonly touched = signal(false);

  protected readonly groupedPermissions = computed<PermissionGroup[]>(() => {
    const permissions = this.availablePermissions();
    const groups: PermissionGroup[] = [
      { key: 'roles', label: 'Roles', permissions: [] },
      { key: 'permisos', label: 'Permisos', permissions: [] },
      { key: 'usuarios', label: 'Usuarios', permissions: [] },
      { key: 'material', label: 'Material', permissions: [] },
      { key: 'other', label: 'Otros', permissions: [] },
    ];

    for (const permission of permissions) {
      const key = this.resolvePermissionGroup(permission.name);
      const targetGroup = groups.find((group) => group.key === key) ?? groups[groups.length - 1];
      targetGroup.permissions.push(permission);
    }

    return groups.filter((group) => group.permissions.length > 0);
  });

  protected readonly hasSelectedPermissionsError = computed(
    () => this.touched() && this.selectedPermissionIds().length === 0,
  );

  constructor() {
    effect(() => {
      this.form.controls.name.setValue(this.initialName(), { emitEvent: false });

      const initialPermissionIds = new Set(this.initialPermissionIds());
      this.selectedPermissionIds.set(
        this.availablePermissions()
          .filter((permission) => initialPermissionIds.has(permission.id))
          .map((permission) => permission.id),
      );
    });
  }

  protected isPermissionSelected(permissionId: string): boolean {
    return this.selectedPermissionIds().includes(permissionId);
  }

  protected togglePermission(permissionId: string, checked: boolean): void {
    const currentSelection = new Set(this.selectedPermissionIds());

    if (checked) {
      currentSelection.add(permissionId);
    } else {
      currentSelection.delete(permissionId);
    }

    this.selectedPermissionIds.set(Array.from(currentSelection));
  }

  protected submit(): void {
    this.touched.set(true);

    if (this.form.invalid || this.selectedPermissionIds().length === 0) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted.emit({
      name: this.form.controls.name.value.trim(),
      permissionIds: this.selectedPermissionIds(),
    });
  }

  protected cancel(): void {
    if (!this.loading()) {
      this.cancelled.emit();
    }
  }

  private resolvePermissionGroup(permissionName: string): PermissionGroupKey {
    if (permissionName.endsWith('_rol')) {
      return 'roles';
    }

    if (permissionName.endsWith('_permiso')) {
      return 'permisos';
    }

    if (permissionName.endsWith('_usuario')) {
      return 'usuarios';
    }

    if (permissionName.endsWith('_material')) {
      return 'material';
    }

    return 'other';
  }
}