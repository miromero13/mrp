import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { AuthService } from '../../../core/users/services/auth.service';
import { CreateWorkShiftFormValue, WorkShiftListItem } from '../../../core/workshifts/models/workshift.models';
import { WorkShiftService } from '../../../core/workshifts/services/workshift.service';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideX } from '@ng-icons/lucide';
import { PERMISOS } from '../../../core/config/permisos';
import { RoleListItem } from '../../../core/users/models/role.models';
import { RoleService } from '../../../core/users/services/role.service';
import { WorkShiftFormComponent } from './workshift-form.component';

@Component({
  selector: 'app-workshifts',
  standalone: true,
  imports: [CustomTableComponent, WorkShiftFormComponent, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucidePlus, lucideX })],
  templateUrl: './workshifts.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkShiftsComponent implements OnInit {
  private readonly workShiftService = inject(WorkShiftService);
  private readonly roleService = inject(RoleService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly loadingRoles = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly workShifts = signal<WorkShiftListItem[]>([]);
  protected readonly roles = signal<RoleListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((p) => p.name) ?? []),
  );


  // ✅ CORREGIDO: permiso correcto
  protected readonly canCreateWorkShift = computed(() =>
    this.adminPermissionNames().has(PERMISOS.workshifts.crear),
  );

  protected readonly assignableRoles = computed<ReadonlyArray<RoleListItem>>(() =>
    this.roles().filter((role) => {
      const rolePermissions = role.permissions ?? [];
      return rolePermissions.every((p) => this.adminPermissionNames().has(p.name));
    }),
  );

  // ✅ CORREGIDO: manejo seguro de fechas
  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<WorkShiftListItem>>>(() => [
    {
      id: 'name',
      header: 'Turno',
      cell: (ws) => ws.name,
    },
    {
      id: 'startDate',
      header: 'Hora de inicio',
      cell: (ws) => new Date(ws.startdate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    },
    {
      id: 'endDate',
      header: 'Hora de finalización',
      cell: (ws) => new Date(ws.enddate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    },
  ]);

  ngOnInit(): void {
    this.loadWorkShifts();
    this.loadRoles();
  }

  protected openCreateModal(): void {
    if (!this.canCreateWorkShift()) return;

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) return;

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(false);
  }

  protected createWorkShift(payload: CreateWorkShiftFormValue): void {
    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.workShiftService
      .createWorkShift(payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateModalOpen.set(false);
          this.loadWorkShifts();
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 401) {
            this.errorMessage.set('Tu sesión expiró. Inicia sesión nuevamente.');
            this.authService.logout();
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set('No tienes permisos para realizar esta acción.');
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo crear el turno.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadWorkShifts(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.workShiftService
      .listWorkShifts()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (data) => this.workShifts.set(data),
        error: (error: HttpErrorResponse) => {
          if (error.status === 401) {
            this.errorMessage.set('Tu sesión expiró. Inicia sesión nuevamente.');
            this.authService.logout();
            return;
          }

          if (error.status === 403) {
            this.errorMessage.set('No tienes permisos para realizar esta acción.');
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los turnos.';
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
            this.errorMessage.set('Tu sesión no es válida. Inicia sesión nuevamente.');
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
