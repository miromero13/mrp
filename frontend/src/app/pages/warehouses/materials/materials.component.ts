import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideX } from '@ng-icons/lucide';
import { BrnTabsImports } from '@spartan-ng/brain/tabs';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { PERMISOS } from '../../../core/config/permisos';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { MaterialService } from '../../../core/enterprises/services/material.service';
import {
  CreateMaterialFormValue,
  CreateMaterialMovementFormValue,
  MaterialListItem,
  MaterialMovementListItem,
  MaterialMovementType,
} from '../../../core/enterprises/models/material.models';
import { AuthService } from '../../../core/users/services/auth.service';

@Component({
  selector: 'app-materials',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...BrnTabsImports, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports],
  providers: [provideIcons({ lucidePlus, lucideX })],
  templateUrl: './materials.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialsComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly materialService = inject(MaterialService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly loadingMovements = signal(true);
  protected readonly saving = signal(false);
  protected readonly movementSaving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly movementErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly isMovementModalOpen = signal(false);
  protected readonly activeTab = signal<'materials' | 'movements'>('materials');
  protected readonly materials = signal<MaterialListItem[]>([]);
  protected readonly movements = signal<MaterialMovementListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canViewMaterialsTab = computed(() => this.adminPermissionNames().has(PERMISOS.material.listar));
  protected readonly canViewMovementsTab = computed(() => this.adminPermissionNames().has(PERMISOS.material.movimientos.listar));
  protected readonly canCreateMaterial = computed(() => this.adminPermissionNames().has(PERMISOS.material.crear));
  protected readonly canCreateMovement = computed(() => this.adminPermissionNames().has(PERMISOS.material.movimientos.crear));
  protected readonly visibleTabs = computed(() =>
    [
      { key: 'materials' as const, visible: this.canViewMaterialsTab() },
      { key: 'movements' as const, visible: this.canViewMovementsTab() },
    ].filter((tab) => tab.visible),
  );

  protected readonly form = this.formBuilder.nonNullable.group({
    code: ['', [Validators.required]],
    unitOfMeasure: ['', [Validators.required]],
    stockMin: [0, [Validators.required, Validators.min(0)]],
    stockCurrent: [0, [Validators.required, Validators.min(0)]],
  });

  protected readonly movementForm = this.formBuilder.nonNullable.group({
    materialId: ['', [Validators.required]],
    type: [MaterialMovementType.ENTRY, [Validators.required]],
    quantity: [0, [Validators.required, Validators.min(0.01)]],
    unitPrice: [0, [Validators.required, Validators.min(0.01)]],
    reason: ['', [Validators.required]],
  });

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<MaterialListItem>>>(() => [
    { id: 'code', header: 'Código', cell: (material) => material.code },
    { id: 'unitOfMeasure', header: 'Unidad', cell: (material) => material.unitOfMeasure },
    { id: 'stockMin', header: 'Stock mín.', cell: (material) => String(material.stockMin) },
    { id: 'stockCurrent', header: 'Stock actual', cell: (material) => String(material.stockCurrent) },
  ]);

  protected readonly movementTableColumns = computed<ReadonlyArray<CustomTableColumn<MaterialMovementListItem>>>(() => [
    { id: 'material', header: 'Material', cell: (movement) => movement.material.code },
    {
      id: 'type',
      header: 'Tipo',
      cell: (movement) => (movement.type === MaterialMovementType.ENTRY ? 'Entrada' : 'Salida'),
    },
    {
      id: 'quantity',
      header: 'Cantidad',
      cell: (movement) => String(movement.details[0]?.quantity ?? 0),
    },
    {
      id: 'unitPrice',
      header: 'Precio unit.',
      cell: (movement) => String(movement.details[0]?.unitPrice ?? 0),
    },
    { id: 'reason', header: 'Motivo', cell: (movement) => movement.reason },
    {
      id: 'createdAt',
      header: 'Fecha',
      cell: (movement) => this.formatDate(movement.createdAt),
    },
  ]);

  ngOnInit(): void {
    this.syncActiveTab();
    this.loadMaterials();
    this.loadMovements();
  }

  protected setActiveTab(tab: string | undefined): void {
    if (tab === 'materials' && this.canViewMaterialsTab()) {
      this.activeTab.set(tab);
      return;
    }

    if (tab === 'movements' && this.canViewMovementsTab()) {
      this.activeTab.set(tab);
    }
  }

  protected openCreateModal(): void {
    if (!this.canCreateMaterial()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.form.reset({ code: '', unitOfMeasure: '', stockMin: 0, stockCurrent: 0 });
    this.isCreateModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(false);
  }

  protected openMovementModal(): void {
    if (!this.canCreateMovement()) {
      return;
    }

    this.movementErrorMessage.set(null);

    const firstMaterialId = this.materials()[0]?.id ?? '';
    this.movementForm.reset({
      materialId: firstMaterialId,
      type: MaterialMovementType.ENTRY,
      quantity: 0,
      unitPrice: 0,
      reason: '',
    });
    this.isMovementModalOpen.set(true);
  }

  protected closeMovementModal(): void {
    if (this.movementSaving()) {
      return;
    }

    this.movementErrorMessage.set(null);
    this.isMovementModalOpen.set(false);
  }

  protected createMaterial(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.createErrorMessage.set(null);

    const value = this.form.getRawValue();
    const payload: CreateMaterialFormValue = {
      code: value.code.trim(),
      unitOfMeasure: value.unitOfMeasure.trim(),
      stockMin: Number(value.stockMin),
      stockCurrent: Number(value.stockCurrent),
    };

    this.materialService
      .createMaterial(payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateModalOpen.set(false);
          this.loadMaterials();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo crear el material.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected createMovement(): void {
    if (this.movementForm.invalid) {
      this.movementForm.markAllAsTouched();
      return;
    }

    this.movementSaving.set(true);
    this.movementErrorMessage.set(null);

    const value = this.movementForm.getRawValue();
    const payload: CreateMaterialMovementFormValue = {
      type: value.type,
      quantity: Number(value.quantity),
      unitPrice: Number(value.unitPrice),
      reason: value.reason.trim(),
    };

    this.materialService
      .createMaterialMovement(value.materialId, payload)
      .pipe(
        take(1),
        finalize(() => this.movementSaving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isMovementModalOpen.set(false);
          this.loadMaterials();
          this.loadMovements();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo registrar el movimiento.';
          this.movementErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadMaterials(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.materialService
      .listMaterials()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (materials) => this.materials.set(materials),
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los materiales.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadMovements(): void {
    this.loadingMovements.set(true);

    this.materialService
      .listMaterialMovements()
      .pipe(
        take(1),
        finalize(() => this.loadingMovements.set(false)),
      )
      .subscribe({
        next: (movements) => this.movements.set(movements),
        error: () => this.movements.set([]),
      });
  }

  protected trackMovement = (index: number): number => index;

  protected movementTypeLabel(type: MaterialMovementType): string {
    return type === MaterialMovementType.ENTRY ? 'Entrada' : 'Salida';
  }

  private syncActiveTab(): void {
    const firstVisibleTab = this.visibleTabs()[0]?.key;
    if (firstVisibleTab) {
      this.activeTab.set(firstVisibleTab);
    }
  }

  private formatDate(value: string): string {
    if (!value) {
      return '-';
    }

    return value.replace('T', ' ').slice(0, 16);
  }
}
