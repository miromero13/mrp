import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { AuthService } from '../../../core/users/services/auth.service';
import { CreateUserFormValue, UserListItem } from '../../../core/users/models/user.models';
import { UserService } from '../../../core/users/services/user.service';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideX } from '@ng-icons/lucide';
import { PERMISOS } from '../../../core/config/permisos';
import { RoleListItem } from '../../../core/users/models/role.models';
import { RoleService } from '../../../core/users/services/role.service';
import { UserFormComponent } from './user-form.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CustomTableComponent, UserFormComponent, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucidePlus, lucideX })],
  templateUrl: './users.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsersComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly roleService = inject(RoleService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly loadingRoles = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly users = signal<UserListItem[]>([]);
  protected readonly roles = signal<RoleListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly isSuperadmin = computed(() => this.authService.currentUser()?.role?.name === 'superadmin');

  protected readonly canCreateUser = computed(() => this.adminPermissionNames().has(PERMISOS.usuarios.crear));

  protected readonly assignableRoles = computed<ReadonlyArray<RoleListItem>>(() =>
    this.roles().filter((role) => {
      if (!this.isSuperadmin() && role.name === 'superadmin') {
        return false;
      }

      if (this.isSuperadmin() && role.name === 'employee') {
        return false;
      }

      const rolePermissions = role.permissions ?? [];
      if (rolePermissions.length === 0) {
        return true;
      }

      return rolePermissions.every((permission) => this.adminPermissionNames().has(permission.name));
    }),
  );

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<UserListItem>>>(() => [
    {
      id: 'name',
      header: 'Usuario',
      cell: (user) => user.name,
    },
    {
      id: 'email',
      header: 'Email',
      cell: (user) => user.email,
    },
    {
      id: 'phone',
      header: 'Telefono',
      cell: (user) => user.phone || '-',
    },
    {
      id: 'role',
      header: 'Rol',
      cell: (user) => user.role?.name || '-',
    },
    {
      id: 'enterprise',
      header: 'Empresa',
      cell: (user) => user.enterprise?.name || '-',
    },
  ]);

  ngOnInit(): void {
    this.loadUsers();
    this.loadRoles();
  }

  protected openCreateModal(): void {
    if (!this.canCreateUser() || this.assignableRoles().length === 0) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(false);
  }

  protected createUser(payload: CreateUserFormValue): void {
    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.userService
      .createUser(payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateModalOpen.set(false);
          this.loadUsers();
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.createErrorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo crear el usuario.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadUsers(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.userService
      .listUsers()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (users) => this.users.set(users),
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.errorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los usuarios.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadRoles(): void {
    this.loadingRoles.set(true);

    this.roleService
      .listRoles()
      .pipe(
        take(1),
        finalize(() => this.loadingRoles.set(false)),
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
          const fallbackError = error?.message ?? 'No se pudieron cargar los roles para crear usuarios.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
