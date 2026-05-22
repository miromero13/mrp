import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { PERMISOS } from '../../../core/config/permisos';
import { AuthService } from '../../../core/users/services/auth.service';
import { EnterpriseService } from '../../../core/enterprises/services/enterprise.service';
import { MaterialService } from '../../../core/enterprises/services/material.service';
import { ProductService } from '../../../core/enterprises/services/product.service';
import { EnterpriseListItem } from '../../../core/enterprises/models/enterprise.models';
import { MaterialListItem } from '../../../core/enterprises/models/material.models';
import { ProductListItem } from '../../../core/enterprises/models/product.models';
import { BatchListItem, BatchStatus, CreateBatchFormValue, UpdateBatchFormValue } from '../../../core/warehouses/models/batch.models';
import { BatchService } from '../../../core/warehouses/services/batch.service';

type AssociationType = 'material' | 'product';

@Component({
  selector: 'app-batches',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  providers: [provideIcons({ lucidePlus, lucideTrash2, lucideX })],
  templateUrl: './batches.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BatchesComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly enterpriseService = inject(EnterpriseService);
  private readonly materialService = inject(MaterialService);
  private readonly productService = inject(ProductService);
  private readonly batchService = inject(BatchService);

  protected readonly loading = signal(true);
  protected readonly loadingCatalog = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formErrorMessage = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly modalMode = signal<'create' | 'edit'>('create');
  protected readonly editingBatchId = signal<string | null>(null);
  protected readonly selectedEnterpriseId = signal<string | null>(null);
  protected readonly enterprises = signal<EnterpriseListItem[]>([]);
  protected readonly materials = signal<MaterialListItem[]>([]);
  protected readonly products = signal<ProductListItem[]>([]);
  protected readonly batches = signal<BatchListItem[]>([]);

  protected readonly currentUserEnterprise = computed(() => this.authService.currentUser()?.enterprise ?? null);
  protected readonly isAdmin = computed(() => (this.authService.currentUser()?.role?.name ?? '') === 'Administrador');

  protected readonly adminPermissionNames = computed(() => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []));
  protected readonly canViewBatches = computed(() => this.isAdmin() && this.adminPermissionNames().has(PERMISOS.warehouses.batches.listar));
  protected readonly canCreateBatch = computed(() => this.isAdmin() && this.adminPermissionNames().has(PERMISOS.warehouses.batches.crear));
  protected readonly canEditBatch = computed(() => this.isAdmin() && this.adminPermissionNames().has(PERMISOS.warehouses.batches.editar));
  protected readonly canDeleteBatch = computed(() => this.isAdmin() && this.adminPermissionNames().has(PERMISOS.warehouses.batches.eliminar));

  protected readonly activeEnterprise = computed(() => {
    return this.currentUserEnterprise();
  });

  protected readonly materialOptions = computed(() => this.materials().map((material) => ({ id: material.id, label: `${material.code} - ${material.unitOfMeasure}` })));
  protected readonly productOptions = computed(() => this.products().map((product) => ({ id: product.id, label: product.name })));

  protected readonly form = this.formBuilder.nonNullable.group({
    associationType: ['material' as AssociationType, [Validators.required]],
    batchNumber: ['', [Validators.required]],
    materialId: ['', []],
    productId: ['', []],
    productionDate: ['', [Validators.required]],
    expirationDate: [''],
    currentStock: [0, [Validators.required, Validators.min(0)]],
    status: [BatchStatus.APPROVED, [Validators.required]],
  });

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<BatchListItem>>>(() => [
    { id: 'batchNumber', header: 'Lote', cell: (batch) => batch.batchNumber },
    { id: 'association', header: 'Asociación', cell: (batch) => this.batchAssociationLabel(batch) },
    { id: 'productionDate', header: 'Ingreso', cell: (batch) => this.formatDate(batch.productionDate) },
    { id: 'expirationDate', header: 'Vence', cell: (batch) => (batch.expirationDate ? this.formatDate(batch.expirationDate) : '-') },
    { id: 'currentStock', header: 'Stock', cell: (batch) => this.formatNumber(batch.currentStock) },
    { id: 'status', header: 'Estado', cell: (batch) => this.statusLabel(batch.status, batch.expired) },
  ]);

  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.loading.set(false);
      this.loadingCatalog.set(false);
      this.errorMessage.set('Solo un administrador puede acceder a lotes.');
      return;
    }

    const currentEnterprise = this.currentUserEnterprise();
    if (currentEnterprise) {
      this.selectedEnterpriseId.set(currentEnterprise.id);
      this.loadCatalog(currentEnterprise.id);
      this.loadBatches(currentEnterprise.id);
      return;
    }

    this.loading.set(false);
    this.loadingCatalog.set(false);
    this.errorMessage.set('No tienes una empresa asignada.');
  }

  protected onEnterpriseChange(enterpriseId: string | null): void {
    if (!this.isAdmin()) {
      return;
    }

    this.selectedEnterpriseId.set(enterpriseId);
    this.loadCatalog(enterpriseId ?? '');
    this.loadBatches(enterpriseId ?? '');
  }

  protected onAssociationTypeChange(value: string | null): void {
    const associationType = (value === 'product' ? 'product' : 'material') as AssociationType;
    this.form.patchValue({ associationType, materialId: '', productId: '' });
  }

  protected onMaterialChange(value: string | null): void {
    this.form.patchValue({ materialId: value ?? '', productId: '' });
  }

  protected onProductChange(value: string | null): void {
    this.form.patchValue({ productId: value ?? '', materialId: '' });
  }

  protected onStatusChange(value: string | null): void {
    this.form.controls.status.setValue((value === 'BLOCKED' ? BatchStatus.BLOCKED : value === 'QUARANTINE' ? BatchStatus.QUARANTINE : BatchStatus.APPROVED) as BatchStatus);
  }

  protected openCreateModal(): void {
    if (!this.canCreateBatch()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set('create');
    this.editingBatchId.set(null);
    this.form.reset({
      associationType: 'material',
      batchNumber: '',
      materialId: this.materialOptions()[0]?.id ?? '',
      productId: '',
      productionDate: this.todayInputValue(),
      expirationDate: '',
      currentStock: 0,
      status: BatchStatus.APPROVED,
    });
    this.isModalOpen.set(true);
  }

  protected openEditModal(batch: BatchListItem): void {
    if (!this.canEditBatch()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set('edit');
    this.editingBatchId.set(batch.id);
    this.form.reset({
      associationType: batch.materialId ? 'material' : 'product',
      batchNumber: batch.batchNumber,
      materialId: batch.materialId ?? '',
      productId: batch.productId ?? '',
      productionDate: batch.productionDate,
      expirationDate: batch.expirationDate ?? '',
      currentStock: batch.currentStock,
      status: batch.status,
    });
    this.isModalOpen.set(true);
  }

  protected closeModal(): void {
    if (this.saving()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.isModalOpen.set(false);
    this.editingBatchId.set(null);
    this.modalMode.set('create');
  }

  protected saveBatch(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const enterpriseId = this.activeEnterprise()?.id;
    if (!enterpriseId) {
      this.formErrorMessage.set('Selecciona una empresa válida.');
      return;
    }

    this.saving.set(true);
    this.formErrorMessage.set(null);

    if (this.modalMode() === 'create') {
      const payload: CreateBatchFormValue = {
        batchNumber: value.batchNumber.trim(),
        materialId: value.associationType === 'material' ? value.materialId : null,
        productId: value.associationType === 'product' ? value.productId : null,
        productionDate: value.productionDate,
        expirationDate: value.expirationDate || null,
        currentStock: Number(value.currentStock),
        status: value.status,
      };

      this.batchService
        .createBatch(payload)
        .pipe(finalize(() => this.saving.set(false)), take(1))
        .subscribe({
          next: () => {
            this.isModalOpen.set(false);
            this.loadBatches(enterpriseId);
          },
          error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudo crear el lote.'),
        });
      return;
    }

    const payload: UpdateBatchFormValue = {
      expirationDate: value.expirationDate || null,
      status: value.status,
    };

    this.batchService
      .updateBatch(this.editingBatchId() as string, payload)
      .pipe(finalize(() => this.saving.set(false)), take(1))
      .subscribe({
        next: () => {
          this.isModalOpen.set(false);
          this.loadBatches(enterpriseId);
        },
        error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudo actualizar el lote.'),
      });
  }

  protected deleteBatch(batch: BatchListItem): void {
    if (!this.canDeleteBatch() || !confirm('¿Eliminar este lote?')) {
      return;
    }

    this.batchService.deleteBatch(batch.id).pipe(take(1)).subscribe({
      next: () => this.loadBatches(this.activeEnterprise()?.id ?? ''),
      error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudo eliminar el lote.'),
    });
  }

  protected loadBatches(enterpriseId: string): void {
    if (!enterpriseId) {
      this.batches.set([]);
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.batchService
      .listBatches({ enterpriseId })
      .pipe(finalize(() => this.loading.set(false)), take(1))
      .subscribe({
        next: (batches) => this.batches.set(batches),
        error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudieron cargar los lotes.'),
      });
  }

  protected trackBatch = (_index: number): number => _index;

  protected batchAssociationLabel(batch: BatchListItem): string {
    if (batch.materialId) {
      return `${batch.materialCode ?? ''}`.trim();
    }

    return batch.productName ?? '-';
  }

  protected statusLabel(status: BatchStatus, expired: boolean): string {
    if (expired) {
      return 'Vencido';
    }

    switch (status) {
      case BatchStatus.BLOCKED:
        return 'Bloqueado';
      case BatchStatus.QUARANTINE:
        return 'Cuarentena';
      default:
        return 'Aprobado';
    }
  }

  protected formatNumber(value: number): string {
    return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
  }

  protected formatDate(value: string): string {
    return new Date(`${value}T00:00:00`).toLocaleDateString('es-CO');
  }

  private loadEnterprises(): void {
    this.enterpriseService
      .listEnterprises()
      .pipe(finalize(() => this.loadingCatalog.set(false)), take(1))
      .subscribe({
        next: (enterprises) => {
          this.enterprises.set(enterprises);
          const selectedEnterpriseId = enterprises[0]?.id ?? null;
          this.selectedEnterpriseId.set(selectedEnterpriseId);
          if (selectedEnterpriseId) {
            this.loadCatalog(selectedEnterpriseId);
            this.loadBatches(selectedEnterpriseId);
          }
        },
        error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudieron cargar las empresas.'),
      });
  }

  private loadCatalog(enterpriseId: string): void {
    if (!enterpriseId) {
      this.materials.set([]);
      this.products.set([]);
      return;
    }

    this.loadingCatalog.set(true);
    this.materialService
      .listMaterials(enterpriseId)
      .pipe(take(1))
      .subscribe({
        next: (materials) => this.materials.set(materials),
        error: () => this.materials.set([]),
      });

    this.productService
      .listProducts(enterpriseId)
      .pipe(finalize(() => this.loadingCatalog.set(false)), take(1))
      .subscribe({
        next: (products) => this.products.set(products),
        error: () => this.products.set([]),
      });
  }

  private handleError(error: HttpErrorResponse, fallbackMessage: string): void {
    const backendError = error?.error?.error ?? error?.error?.message;
    const fallbackError = error?.message ?? fallbackMessage;
    this.errorMessage.set(backendError ?? fallbackError);
  }

  private todayInputValue(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
