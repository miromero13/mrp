import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { AuthService } from '../../../core/users/services/auth.service';
import { CreateWorkShiftFormValue, WorkShiftListItem } from '../../../core/workshifts/models/workshift.models';
import { WorkShiftService } from '../../../core/workshifts/services/workshift.service';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { provideIcons } from '@ng-icons/core';
import { lucideEye, lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { PERMISOS } from '../../../core/config/permisos';
import { WorkShiftFormComponent } from './workshift-form.component';

@Component({
  selector: 'app-workshifts',
  standalone: true,
  imports: [CustomTableComponent, WorkShiftFormComponent, ...HlmButtonImports, ...HlmIconImports],
  providers: [provideIcons({ lucideEye, lucidePencil, lucidePlus, lucideTrash2, lucideX })],
  templateUrl: './workshifts.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkShiftsComponent implements OnInit {
  @ViewChild('workshiftActions', { static: true })
  private readonly workshiftActionsTemplate!: TemplateRef<{ $implicit: WorkShiftListItem }>;

  private readonly workShiftService = inject(WorkShiftService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly isEditModalOpen = signal(false);
  protected readonly modalMode = signal<'create' | 'view' | 'edit' | null>(null);
  protected readonly selectedWorkShift = signal<WorkShiftListItem | null>(null);
  protected readonly workShifts = signal<WorkShiftListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((p) => p.name) ?? []),
  );


  // ✅ CORREGIDO: permiso correcto
  protected readonly canCreateWorkShift = computed(() =>
    this.adminPermissionNames().has(PERMISOS.workshifts.crear),
  );
  protected readonly canViewWorkShift = computed(() => this.adminPermissionNames().has(PERMISOS.workshifts.listar));
  protected readonly canEditWorkShift = computed(() => this.adminPermissionNames().has(PERMISOS.workshifts.editar));
  protected readonly canDeleteWorkShift = computed(() => this.adminPermissionNames().has(PERMISOS.workshifts.eliminar));

  // ✅ CORREGIDO: manejo seguro de fechas
  protected tableColumns: ReadonlyArray<CustomTableColumn<WorkShiftListItem>> = [];

  ngOnInit(): void {
    this.tableColumns = [
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
      {
        id: 'actions',
        header: 'Acciones',
        cell: () => '',
        template: this.workshiftActionsTemplate,
        align: 'right',
      },
    ];
    this.loadWorkShifts();
  }

  protected openCreateModal(): void {
    if (!this.canCreateWorkShift()) return;

    this.createErrorMessage.set(null);
    this.modalMode.set('create');
    this.isCreateModalOpen.set(true);
  }

  protected openWorkShiftModal(workShift: WorkShiftListItem, mode: 'view' | 'edit'): void {
    if (mode === 'view' && !this.canViewWorkShift()) {
      return;
    }

    if (mode === 'edit' && !this.canEditWorkShift()) {
      return;
    }

    this.selectedWorkShift.set(workShift);
    this.modalMode.set(mode);
    this.createErrorMessage.set(null);
    this.isEditModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) return;

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(false);
    this.modalMode.set(null);
  }

  protected closeEditModal(): void {
    if (this.saving()) return;

    this.createErrorMessage.set(null);
    this.isEditModalOpen.set(false);
    this.modalMode.set(null);
    this.selectedWorkShift.set(null);
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
          if (error.status === 401 || error.status === 403) {
            this.errorMessage.set('Tu sesión expiró. Inicia sesión nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo crear el turno.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected updateWorkShift(payload: CreateWorkShiftFormValue): void {
    const workShift = this.selectedWorkShift();
    if (!workShift) {
      return;
    }

    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.workShiftService
      .updateWorkShift(workShift.id, payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.closeEditModal();
          this.loadWorkShifts();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo actualizar el turno.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deleteWorkShift(workShift: WorkShiftListItem): void {
    if (!this.canDeleteWorkShift() || !confirm('¿Eliminar este turno?')) {
      return;
    }

    this.workShiftService.deleteWorkShift(workShift.id).pipe(take(1)).subscribe({
      next: () => this.loadWorkShifts(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar el turno.';
        this.errorMessage.set(backendError ?? fallbackError);
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
          if (error.status === 401 || error.status === 403) {
            this.errorMessage.set('Tu sesión expiró. Inicia sesión nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los turnos.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
