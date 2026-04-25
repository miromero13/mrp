import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { AuthService } from '../../../core/users/services/auth.service';
import { CreateUserFormValue, UpdateUserFormValue, UserListItem } from '../../../core/users/models/user.models';
import { UserService } from '../../../core/users/services/user.service';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { provideIcons } from '@ng-icons/core';
import { lucideEye, lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { PERMISOS } from '../../../core/config/permisos';
import { RoleListItem } from '../../../core/users/models/role.models';
import { RoleService } from '../../../core/users/services/role.service';
import { UserFormComponent } from './user-form.component';

@Component({
	selector: 'app-user',
	standalone: true,
	imports: [CustomTableComponent, UserFormComponent, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucideEye, lucidePencil, lucidePlus, lucideTrash2, lucideX })],
	templateUrl: './user.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserComponent implements OnInit {
  @ViewChild('userActions', { static: true })
  private readonly userActionsTemplate!: TemplateRef<{ $implicit: UserListItem }>;

  private readonly userService = inject(UserService);
  private readonly roleService = inject(RoleService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly loadingRoles = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly isEditModalOpen = signal(false);
  protected readonly modalMode = signal<'view' | 'edit' | null>(null);
  protected readonly selectedUser = signal<UserListItem | null>(null);
  protected readonly users = signal<UserListItem[]>([]);
  protected readonly roles = signal<RoleListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly isSuperadmin = computed(() => this.authService.currentUser()?.role?.name === 'superadmin');

  protected readonly canCreateUser = computed(() => this.adminPermissionNames().has(PERMISOS.usuarios.crear));
  protected readonly canViewUser = computed(() => this.adminPermissionNames().has(PERMISOS.usuarios.listar));
  protected readonly canEditUser = computed(() => this.adminPermissionNames().has(PERMISOS.usuarios.editar));
  protected readonly canDeleteUser = computed(() => this.adminPermissionNames().has(PERMISOS.usuarios.eliminar));

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

  protected tableColumns: ReadonlyArray<CustomTableColumn<UserListItem>> = [];

  ngOnInit(): void {
    this.tableColumns = [
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
      {
        id: 'actions',
        header: 'Acciones',
        cell: () => '',
        template: this.userActionsTemplate,
        align: 'right',
      },
    ];
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

  protected openUserModal(user: UserListItem, mode: 'view' | 'edit'): void {
    if (mode === 'view' && !this.canViewUser()) {
      return;
    }

    if (mode === 'edit' && !this.canEditUser()) {
      return;
    }

    this.selectedUser.set(user);
    this.modalMode.set(mode);
    this.createErrorMessage.set(null);
    this.isEditModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(false);
  }

  protected closeEditModal(): void {
    if (this.saving()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isEditModalOpen.set(false);
    this.modalMode.set(null);
    this.selectedUser.set(null);
  }

  protected createUser(payload: CreateUserFormValue | UpdateUserFormValue): void {
    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.userService
      .createUser(payload as CreateUserFormValue)
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

  protected updateUser(payload: CreateUserFormValue | UpdateUserFormValue): void {
    const user = this.selectedUser();
    if (!user) {
      return;
    }

    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.userService
      .updateUser(user.id, payload as UpdateUserFormValue)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.closeEditModal();
          this.loadUsers();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo actualizar el usuario.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deleteUser(user: UserListItem): void {
    if (!this.canDeleteUser() || !confirm('¿Eliminar este usuario?')) {
      return;
    }

    this.userService.deleteUser(user.id).pipe(take(1)).subscribe({
      next: () => this.loadUsers(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar el usuario.';
        this.errorMessage.set(backendError ?? fallbackError);
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
