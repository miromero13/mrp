import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { RoleListItem } from '../../../core/users/models/role.models';
import { AuthService } from '../../../core/users/services/auth.service';
import { RoleService } from '../../../core/users/services/role.service';
import { RoleFormComponent } from './role-form.component';
import { PERMISOS } from '../../../core/config/permisos';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { provideIcons } from '@ng-icons/core';
import { lucideEye, lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { PermissionListItem } from '../../../core/users/models/permission.models';
import { RoleFormValue } from '../../../core/users/models/role.models';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [CustomTableComponent, RoleFormComponent, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucideEye, lucidePencil, lucidePlus, lucideTrash2, lucideX })],
  templateUrl: './roles.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RolesComponent implements OnInit {
  protected readonly PERMISOS = PERMISOS;

  @ViewChild('roleActions', { static: true })
  private readonly roleActionsTemplate!: TemplateRef<{ $implicit: RoleListItem }>;

  private readonly roleService = inject(RoleService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly isEditModalOpen = signal(false);
  protected readonly modalMode = signal<'view' | 'edit' | null>(null);
  protected readonly selectedRole = signal<RoleListItem | null>(null);
  protected readonly roles = signal<RoleListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canCreateRole = computed(() => this.adminPermissionNames().has(PERMISOS.roles.crear));
  protected readonly availablePermissions = computed<ReadonlyArray<PermissionListItem>>(() =>
    (this.authService.currentUser()?.role?.permissions ?? []).filter((permission) => this.adminPermissionNames().has(permission.name)),
  );

  protected tableColumns: ReadonlyArray<CustomTableColumn<RoleListItem>> = [];

  ngOnInit(): void {
    this.tableColumns = [
      {
        id: 'name',
        header: 'Rol',
        cell: (role) => role.name,
      },
      {
        id: 'permissions',
        header: 'Permisos',
        cell: (role) => {
          const totalPermissions = role.permissions?.length ?? 0;

          return totalPermissions > 0 ? `${totalPermissions} permiso${totalPermissions === 1 ? '' : 's'}` : 'Sin permisos';
        },
      },
      {
        id: 'actions',
        header: 'Acciones',
        cell: () => '',
        template: this.roleActionsTemplate,
        align: 'right',
      },
    ];
    this.loadRoles();
  }

  protected openCreateModal(): void {
    if (!this.canCreateRole() || this.availablePermissions().length === 0) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(true);
  }

  protected openRoleModal(role: RoleListItem, mode: 'view' | 'edit'): void {
    if (mode === 'view' && !this.adminPermissionNames().has(PERMISOS.roles.listar)) {
      return;
    }

    if (mode === 'edit' && !this.adminPermissionNames().has(PERMISOS.roles.editar)) {
      return;
    }

    this.selectedRole.set(role);
    this.modalMode.set(mode);
    this.createErrorMessage.set(null);
    this.isEditModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) {
      return;
    }

    this.isCreateModalOpen.set(false);
    this.createErrorMessage.set(null);
  }

  protected closeEditModal(): void {
    if (this.saving()) {
      return;
    }

    this.isEditModalOpen.set(false);
    this.selectedRole.set(null);
    this.modalMode.set(null);
    this.createErrorMessage.set(null);
  }

  protected createRole(payload: RoleFormValue): void {
    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.roleService
      .createRole(payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateModalOpen.set(false);
          this.loadRoles();
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.createErrorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo crear el rol.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected updateRole(payload: RoleFormValue): void {
    const role = this.selectedRole();
    if (!role) {
      return;
    }

    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.roleService
      .updateRole(role.id, payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.closeEditModal();
          this.loadRoles();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo actualizar el rol.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deleteRole(role: RoleListItem): void {
    if (!this.adminPermissionNames().has(PERMISOS.roles.eliminar) || !confirm('¿Eliminar este rol?')) {
      return;
    }

    this.roleService.deleteRole(role.id).pipe(take(1)).subscribe({
      next: () => this.loadRoles(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar el rol.';
        this.errorMessage.set(backendError ?? fallbackError);
      },
    });
  }

  protected loadRoles(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.roleService
      .listRoles()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (roles) => this.roles.set(roles),
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.errorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los roles.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
