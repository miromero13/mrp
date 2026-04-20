import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
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
import { lucidePlus, lucideX } from '@ng-icons/lucide';
import { PermissionListItem } from '../../../core/users/models/permission.models';
import { RoleFormValue } from '../../../core/users/models/role.models';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [CustomTableComponent, RoleFormComponent, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucidePlus, lucideX })],
  templateUrl: './roles.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RolesComponent implements OnInit {
  private readonly roleService = inject(RoleService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly roles = signal<RoleListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canCreateRole = computed(() => this.adminPermissionNames().has(PERMISOS.roles.crear));
  protected readonly availablePermissions = computed<ReadonlyArray<PermissionListItem>>(() =>
    (this.authService.currentUser()?.role?.permissions ?? []).filter((permission) => this.adminPermissionNames().has(permission.name)),
  );

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<RoleListItem>>>(() => [
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
  ]);

  ngOnInit(): void {
    this.loadRoles();
  }

  protected openCreateModal(): void {
    if (!this.canCreateRole() || this.availablePermissions().length === 0) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) {
      return;
    }

    this.isCreateModalOpen.set(false);
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