import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucideEye, lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { PERMISOS } from '../../../core/config/permisos';
import { MachineFormValue, MachineListItem } from '../../../core/enterprises/models/machine.models';
import { MachineService } from '../../../core/enterprises/services/machine.service';
import { AuthService } from '../../../core/users/services/auth.service';

@Component({
  selector: 'app-machines',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports],
  providers: [provideIcons({ lucideEye, lucidePlus, lucidePencil, lucideTrash2, lucideX })],
  templateUrl: './machines.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MachinesComponent implements OnInit {
  @ViewChild('machineActions', { static: true })
  private readonly machineActionsTemplate!: TemplateRef<{ $implicit: MachineListItem }>;

  private readonly formBuilder = inject(FormBuilder);
  private readonly machineService = inject(MachineService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formErrorMessage = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly modalMode = signal<'create' | 'view' | 'edit'>('create');
  protected readonly editingMachineId = signal<string | null>(null);
  protected readonly selectedMachine = signal<MachineListItem | null>(null);
  protected readonly machines = signal<MachineListItem[]>([]);
  protected tableColumns: ReadonlyArray<CustomTableColumn<MachineListItem>> = [];

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canViewMachine = computed(() => this.adminPermissionNames().has(PERMISOS.machines.listar));
  protected readonly canEditMachine = computed(() => this.adminPermissionNames().has(PERMISOS.machines.editar));
  protected readonly canDeleteMachine = computed(() => this.adminPermissionNames().has(PERMISOS.machines.eliminar));
  protected readonly canCreateMachine = computed(() => this.adminPermissionNames().has(PERMISOS.machines.crear));
  protected readonly isReadOnly = computed(() => this.modalMode() === 'view');

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(255)]],
    cost: [0, [Validators.required, Validators.min(0)]],
  });

  ngOnInit(): void {
    this.tableColumns = [
      { id: 'name', header: 'Nombre', cell: (machine) => machine.name },
      { id: 'description', header: 'Descripción', cell: (machine) => machine.description ?? '-' },
      { id: 'cost', header: 'Costo', cell: (machine) => String(machine.cost) },
      {
        id: 'actions',
        header: 'Acciones',
        cell: () => '',
        template: this.machineActionsTemplate,
        align: 'right',
      },
    ];
    this.loadMachines();
  }

  protected openModal(machine: MachineListItem | null = null): void {
    this.openMachineModal(machine, machine ? 'edit' : 'create');
  }

  protected openMachineModal(machine: MachineListItem | null, mode: 'create' | 'view' | 'edit'): void {
    if (mode === 'create' && !this.canCreateMachine()) {
      return;
    }

    if (mode === 'view' && !this.canViewMachine()) {
      return;
    }

    if (mode === 'edit' && !this.canEditMachine()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set(mode);
    this.editingMachineId.set(machine?.id ?? null);
    this.selectedMachine.set(machine);
    this.form.reset({
      name: machine?.name ?? '',
      description: machine?.description ?? '',
      cost: machine?.cost ?? 0,
    });
    this.isModalOpen.set(true);
  }

  protected closeModal(): void {
    if (this.saving()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.isModalOpen.set(false);
    this.editingMachineId.set(null);
    this.selectedMachine.set(null);
    this.modalMode.set('create');
  }

  protected saveMachine(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: MachineFormValue = {
      name: value.name.trim(),
      description: value.description.trim() || null,
      cost: Number(value.cost),
    };

    this.saving.set(true);
    this.formErrorMessage.set(null);

    const request$ = this.editingMachineId()
      ? this.machineService.updateMachine(this.editingMachineId() as string, payload)
      : this.machineService.createMachine(payload);

    request$
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isModalOpen.set(false);
          this.editingMachineId.set(null);
          this.selectedMachine.set(null);
          this.loadMachines();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo guardar la maquinaria.';
          this.formErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deleteMachine(id: string): void {
    if (!this.canDeleteMachine() || !confirm('¿Eliminar esta maquinaria?')) {
      return;
    }

    this.machineService.deleteMachine(id).pipe(take(1)).subscribe({
      next: () => this.loadMachines(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar la maquinaria.';
        this.errorMessage.set(backendError ?? fallbackError);
      },
    });
  }

  protected loadMachines(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.machineService
      .listMachines()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (machines) => this.machines.set(machines),
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar las maquinarias.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
