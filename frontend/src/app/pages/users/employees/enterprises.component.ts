import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { AuthService } from '../../../core/users/services/auth.service';
import { CreateEnterpriseFormValue, EnterpriseListItem } from '../../../core/enterprises/models/enterprise.models';
import { EnterpriseService } from '../../../core/enterprises/services/enterprise.service';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideX } from '@ng-icons/lucide';
import { PERMISOS } from '../../../core/config/permisos';
import { RoleListItem } from '../../../core/users/models/role.models';
import { RoleService } from '../../../core/users/services/role.service';
import { EnterpriseFormComponent } from './enterprise-form.component';

@Component({
  selector: 'app-enterprises',
  standalone: true,
  imports: [CustomTableComponent, EnterpriseFormComponent, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucidePlus, lucideX })],
  templateUrl: './enterprises.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnterprisesComponent implements OnInit {
  private readonly enterpriseService = inject(EnterpriseService);
  private readonly roleService = inject(RoleService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly loadingRoles = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly enterprises = signal<EnterpriseListItem[]>([]);
  protected readonly roles = signal<RoleListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canCreateEnterprise = computed(() => this.adminPermissionNames().has(PERMISOS.usuarios.crear));

  protected readonly assignableRoles = computed<ReadonlyArray<RoleListItem>>(() =>
    this.roles().filter((role) => {
      const rolePermissions = role.permissions ?? [];
      if (rolePermissions.length === 0) {
        return true;
      }

      return rolePermissions.every((permission) => this.adminPermissionNames().has(permission.name));
    }),
  );

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<EnterpriseListItem>>>(() => [
    {
      id: 'name',
      header: 'Empresa',
      cell: (enterprise) => enterprise.name,
    },
    {
      id: 'nit',
      header: 'NIT',
      cell: (enterprise) => enterprise.nit,
    },
    {
      id: 'address',
      header: 'Dirección',
      cell: (enterprise) => enterprise.address ?? '-',
    },
  ]);

  ngOnInit(): void {
    this.loadEnterprises();
    this.loadRoles();
  }

  protected openCreateModal(): void {
    if (!this.canCreateEnterprise() || this.assignableRoles().length === 0) {
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

  protected createEnterprise(payload: CreateEnterpriseFormValue): void {
    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.enterpriseService
      .createEnterprise(payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateModalOpen.set(false);
          this.loadEnterprises();
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

  protected loadEnterprises(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.enterpriseService
      .listEnterprises()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (enterprises) => this.enterprises.set(enterprises),
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.errorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar las empresas.';
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
