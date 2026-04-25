import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { provideIcons } from '@ng-icons/core';
import { lucideEye, lucidePencil, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { finalize, take } from 'rxjs/operators';
import { PermissionListItem } from '../../../core/users/models/permission.models';
import { AuthService } from '../../../core/users/services/auth.service';
import { PermissionService } from '../../../core/users/services/permission.service';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { PERMISOS } from '../../../core/config/permisos';

@Component({
  selector: 'app-permissions',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports],
  providers: [provideIcons({ lucideEye, lucidePencil, lucideTrash2, lucideX })],
  templateUrl: './permissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermissionsComponent implements OnInit {
  @ViewChild('permissionActions', { static: true })
  private readonly permissionActionsTemplate!: TemplateRef<{ $implicit: PermissionListItem }>;

  private readonly formBuilder = inject(FormBuilder);
  private readonly permissionService = inject(PermissionService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formErrorMessage = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly modalMode = signal<'view' | 'edit' | null>(null);
  protected readonly selectedPermission = signal<PermissionListItem | null>(null);
  protected readonly permissions = signal<PermissionListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canViewPermission = computed(() => this.adminPermissionNames().has(PERMISOS.permisos.listar));
  protected readonly canEditPermission = computed(() => this.adminPermissionNames().has(PERMISOS.permisos.editar));
  protected readonly canDeletePermission = computed(() => this.adminPermissionNames().has(PERMISOS.permisos.eliminar));

  protected tableColumns: ReadonlyArray<CustomTableColumn<PermissionListItem>> = [];

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(300)]],
  });

  protected readonly isReadOnly = computed(() => this.modalMode() === 'view');

  ngOnInit(): void {
    this.tableColumns = [
      {
        id: 'name',
        header: 'Permiso',
        cell: (permission) => permission.name,
      },
      {
        id: 'description',
        header: 'Descripcion',
        cell: (permission) => permission.description || '-',
      },
      {
        id: 'actions',
        header: 'Acciones',
        cell: () => '',
        template: this.permissionActionsTemplate,
        align: 'right',
      },
    ];
    this.loadPermissions();
  }

  protected openPermissionModal(permission: PermissionListItem | null, mode: 'view' | 'edit'): void {
    if (mode === 'view' && !this.canViewPermission()) {
      return;
    }

    if (mode === 'edit' && !this.canEditPermission()) {
      return;
    }

    this.selectedPermission.set(permission);
    this.modalMode.set(mode);
    this.formErrorMessage.set(null);
    this.form.reset({
      name: permission?.name ?? '',
      description: permission?.description ?? '',
    });
    this.isModalOpen.set(true);
  }

  protected closeModal(): void {
    if (this.saving()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.isModalOpen.set(false);
    this.modalMode.set(null);
    this.selectedPermission.set(null);
  }

  protected savePermission(): void {
    if (this.isReadOnly() || !this.canEditPermission()) {
      return;
    }

    if (this.form.invalid || !this.selectedPermission()) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload = {
      name: value.name.trim(),
      description: value.description.trim(),
    };

    this.saving.set(true);
    this.formErrorMessage.set(null);

    this.permissionService
      .updatePermission(this.selectedPermission()!.id, payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.closeModal();
          this.loadPermissions();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo guardar el permiso.';
          this.formErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deletePermission(permission: PermissionListItem): void {
    if (!this.canDeletePermission() || !confirm('¿Eliminar este permiso?')) {
      return;
    }

    this.permissionService.deletePermission(permission.id).pipe(take(1)).subscribe({
      next: () => this.loadPermissions(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar el permiso.';
        this.errorMessage.set(backendError ?? fallbackError);
      },
    });
  }

  protected loadPermissions(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.permissionService
      .listPermissions()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (permissions) => this.permissions.set(permissions),
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.errorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los permisos.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
