import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { PERMISOS } from '../../../core/config/permisos';
import { AuthService } from '../../../core/users/services/auth.service';
import { BomItemListItem, CreateBomItemFormValue } from '../../../core/enterprises/models/bom.models';
import { BomService } from '../../../core/enterprises/services/bom.service';
import { MaterialService } from '../../../core/enterprises/services/material.service';
import { ProductService } from '../../../core/enterprises/services/product.service';
import { MaterialListItem } from '../../../core/enterprises/models/material.models';
import { ProductListItem } from '../../../core/enterprises/models/product.models';

@Component({
  selector: 'app-bom',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  providers: [provideIcons({ lucidePencil, lucidePlus, lucideTrash2, lucideX })],
  templateUrl: './bom.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BomComponent implements OnInit {
  @ViewChild('actionsTemplate', { static: true })
  private readonly actionsTemplate!: TemplateRef<{ $implicit: BomItemListItem }>;

  private readonly formBuilder = inject(FormBuilder);
  private readonly bomService = inject(BomService);
  private readonly productService = inject(ProductService);
  private readonly materialService = inject(MaterialService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly loadingProducts = signal(true);
  protected readonly loadingMaterials = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formErrorMessage = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly modalMode = signal<'create' | 'edit'>('create');
  protected readonly editingItemId = signal<string | null>(null);
  protected readonly selectedVersionId = signal<string>('');
  protected readonly bomItems = signal<BomItemListItem[]>([]);
  protected readonly products = signal<ProductListItem[]>([]);
  protected readonly materials = signal<MaterialListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canViewBom = computed(() => this.adminPermissionNames().has(PERMISOS.bom.listar));
  protected readonly canCreateBom = computed(() => this.adminPermissionNames().has(PERMISOS.bom.crear));
  protected readonly canEditBom = computed(() => this.adminPermissionNames().has(PERMISOS.bom.editar));
  protected readonly canDeleteBom = computed(() => this.adminPermissionNames().has(PERMISOS.bom.eliminar));

  protected readonly versionOptions = computed(() =>
    this.products().flatMap((product) =>
      product.versions.map((version) => ({
        id: version.id,
        label: `${product.name} - ${version.name}`,
        approved: version.approved,
        usedInProduction: version.usedInProduction,
      })),
    ),
  );

  protected readonly selectedVersion = computed(() => this.versionOptions().find((version) => version.id === this.selectedVersionId()) ?? null);
  protected readonly selectedVersionLocked = computed(() => Boolean(this.selectedVersion()?.approved || this.selectedVersion()?.usedInProduction));
  protected readonly totalEstimatedCost = computed(() =>
    this.bomItems().reduce((sum, item) => sum + Number(item.estimatedCost ?? 0), 0).toFixed(2),
  );

  protected readonly form = this.formBuilder.nonNullable.group({
    productVersionId: ['', [Validators.required]],
    materialId: ['', [Validators.required]],
    quantity: [0, [Validators.required, Validators.min(0.0001)]],
    scrapFactor: [0, [Validators.required, Validators.min(0)]],
  });

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<BomItemListItem>>>(() => [
    { id: 'material', header: 'Material', cell: (item) => `${item.materialCode} (${item.materialUnitOfMeasure})` },
    { id: 'quantity', header: 'Cantidad', cell: (item) => String(item.quantity) },
    { id: 'scrapFactor', header: 'Merma %', cell: (item) => String(item.scrapFactor) },
    { id: 'unitPrice', header: 'Precio unit.', cell: (item) => String(item.materialUnitPrice) },
    { id: 'estimatedCost', header: 'Costo estimado', cell: (item) => String(item.estimatedCost) },
    {
      id: 'actions',
      header: 'Acciones',
      cell: () => '',
      template: this.actionsTemplate,
      align: 'right',
    },
  ]);

  protected readonly canEditSelectedVersion = computed(() => Boolean(this.canCreateBom() || this.canEditBom()) && !this.selectedVersionLocked());

  ngOnInit(): void {
    this.loadProducts();
    this.loadMaterials();
  }

  protected openCreateModal(): void {
    if (!this.canCreateBom() || this.selectedVersionLocked() || !this.selectedVersionId()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set('create');
    this.editingItemId.set(null);
    this.form.reset({
      productVersionId: this.selectedVersionId(),
      materialId: this.materials()[0]?.id ?? '',
      quantity: 0,
      scrapFactor: 0,
    });
    this.isModalOpen.set(true);
  }

  protected openEditModal(item: BomItemListItem): void {
    if (!this.canEditBom() || this.selectedVersionLocked()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set('edit');
    this.editingItemId.set(item.id);
    this.form.reset({
      productVersionId: item.productVersionId,
      materialId: item.materialId,
      quantity: item.quantity,
      scrapFactor: item.scrapFactor,
    });
    this.isModalOpen.set(true);
  }

  protected closeModal(): void {
    if (this.saving()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.isModalOpen.set(false);
    this.editingItemId.set(null);
    this.modalMode.set('create');
  }

  protected saveBomItem(): void {
    if (this.form.invalid || this.selectedVersionLocked()) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.formErrorMessage.set(null);

    const value = this.form.getRawValue();
    const payload: CreateBomItemFormValue = {
      productVersionId: value.productVersionId,
      materialId: value.materialId,
      quantity: Number(value.quantity),
      scrapFactor: Number(value.scrapFactor),
    };

    const request$ = this.modalMode() === 'edit' && this.editingItemId()
      ? this.bomService.updateBomItem(this.editingItemId() as string, payload)
      : this.bomService.createBomItem(payload);

    request$
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isModalOpen.set(false);
          this.editingItemId.set(null);
          this.loadBomItems();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo guardar el componente BOM.';
          this.formErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deleteBomItem(item: BomItemListItem): void {
    if (!this.canDeleteBom() || this.selectedVersionLocked() || !confirm('¿Eliminar este componente del BOM?')) {
      return;
    }

    this.bomService.deleteBomItem(item.id).pipe(take(1)).subscribe({
      next: () => this.loadBomItems(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar el componente BOM.';
        this.errorMessage.set(backendError ?? fallbackError);
      },
    });
  }

  protected onVersionChange(versionId: string | null): void {
    const safeVersionId = versionId ?? '';
    this.selectedVersionId.set(safeVersionId);
    this.form.patchValue({ productVersionId: safeVersionId });
    this.loadBomItems();
  }

  protected onMaterialChange(materialId: string | null): void {
    this.form.patchValue({ materialId: materialId ?? '' });
  }

  protected loadBomItems(): void {
    const versionId = this.selectedVersionId();
    if (!versionId) {
      this.bomItems.set([]);
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.bomService
      .listBomItems(versionId)
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (items) => this.bomItems.set(items),
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo cargar el BOM.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadProducts(): void {
    this.loadingProducts.set(true);

    this.productService
      .listProducts()
      .pipe(
        take(1),
        finalize(() => this.loadingProducts.set(false)),
      )
      .subscribe({
        next: (products) => {
          this.products.set(products);
          const firstVersion = products.flatMap((product) => product.versions)[0];
          if (firstVersion) {
            this.selectedVersionId.set(firstVersion.id);
            this.form.patchValue({ productVersionId: firstVersion.id });
            this.loadBomItems();
            return;
          }

          this.bomItems.set([]);
          this.loading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los productos.';
          this.errorMessage.set(backendError ?? fallbackError);
          this.loading.set(false);
        },
      });
  }

  protected loadMaterials(): void {
    this.loadingMaterials.set(true);

    this.materialService
      .listMaterials()
      .pipe(
        take(1),
        finalize(() => this.loadingMaterials.set(false)),
      )
      .subscribe({
        next: (materials) => this.materials.set(materials),
        error: () => this.materials.set([]),
      });
  }

  protected trackItem = (index: number): number => index;

  protected readonly selectedMaterialLabel = computed(() => {
    const material = this.materials().find(
      m => m.id === this.form.controls.materialId.value
    );

    if (!material) {
      return 'Selecciona un material';
    }

    return `${material.code} - ${material.unitOfMeasure} - ${material.unitPrice}`;
  });
}
