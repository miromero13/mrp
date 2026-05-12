import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucideMinus, lucidePlus, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { MaterialService } from '../../../core/enterprises/services/material.service';
import { ProductService } from '../../../core/enterprises/services/product.service';
import { CreateProductFormValue, ProductListItem } from '../../../core/enterprises/models/product.models';
import { MaterialListItem } from '../../../core/enterprises/models/material.models';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports],
  providers: [provideIcons({ lucideMinus, lucidePlus, lucideX })],
  templateUrl: './products.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductsComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly productService = inject(ProductService);
  private readonly materialService = inject(MaterialService);

  protected readonly loading = signal(true);
  protected readonly loadingMaterials = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateModalOpen = signal(false);
  protected readonly products = signal<ProductListItem[]>([]);
  protected readonly materials = signal<MaterialListItem[]>([]);

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required]],
    description: [''],
    productionCost: [0, [Validators.required, Validators.min(0)]],
    salePrice: [0, [Validators.required, Validators.min(0)]],
    materialIds: this.formBuilder.nonNullable.control<string[]>([], [Validators.required]),
    versions: this.formBuilder.array([this.createVersionGroup()]),
  });

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<ProductListItem>>>(() => [
    { id: 'name', header: 'Producto', cell: (product) => product.name },
    { id: 'description', header: 'Descripción', cell: (product) => product.description ?? '-' },
    { id: 'productionCost', header: 'Costo prod.', cell: (product) => String(product.productionCost) },
    { id: 'salePrice', header: 'Precio venta', cell: (product) => String(product.salePrice) },
    {
      id: 'materials',
      header: 'Materiales',
      cell: (product) => product.materials.map((material) => material.code).join(', ') || '-',
    },
    {
      id: 'versions',
      header: 'Versiones',
      cell: (product) => product.versions.map((version) => version.name).join(', ') || '-',
    },
  ]);

  get versions(): FormArray {
    return this.form.controls.versions;
  }

  get versionGroups(): FormGroup[] {
    return this.versions.controls as FormGroup[];
  }

  ngOnInit(): void {
    this.loadProducts();
    this.loadMaterials();
  }

  protected openCreateModal(): void {
    this.createErrorMessage.set(null);
    this.form.reset({
      name: '',
      description: '',
      productionCost: 0,
      salePrice: 0,
      materialIds: [],
    });
    this.versions.clear();
    this.addVersion();
    this.isCreateModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateModalOpen.set(false);
  }

  protected addVersion(): void {
    this.versions.push(this.createVersionGroup());
  }

  protected removeVersion(index: number): void {
    if (this.versions.length > 1) {
      this.versions.removeAt(index);
      return;
    }

    this.versions.at(0)?.get('name')?.setValue('');
  }

  protected toggleMaterial(materialId: string, checked: boolean): void {
    const current = this.form.controls.materialIds.value;
    const next = checked ? [...current, materialId] : current.filter((id) => id !== materialId);
    this.form.controls.materialIds.setValue(next);
    this.form.controls.materialIds.markAsDirty();
    this.form.controls.materialIds.markAsTouched();
  }

  protected isMaterialSelected(materialId: string): boolean {
    return this.form.controls.materialIds.value.includes(materialId);
  }

  protected createProduct(): void {
    if (this.form.invalid || this.versions.length === 0) {
      this.form.markAllAsTouched();
      return;
    }

    const materialIds = this.form.controls.materialIds.value;
    if (materialIds.length === 0) {
      this.createErrorMessage.set('Debes seleccionar al menos un material.');
      return;
    }

    const versionNames = this.versions.controls
      .map((group) => String(group.get('name')?.value ?? '').trim())
      .filter((name) => name.length > 0);

    if (versionNames.length === 0) {
      this.createErrorMessage.set('Debes agregar al menos una versión.');
      return;
    }

    this.saving.set(true);
    this.createErrorMessage.set(null);

    const value = this.form.getRawValue();
    const payload: CreateProductFormValue = {
      name: value.name.trim(),
      description: value.description.trim() || null,
      productionCost: Number(value.productionCost),
      salePrice: Number(value.salePrice),
      materialIds,
      versions: versionNames.map((name) => ({ name })),
    };

    this.productService
      .createProduct(payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateModalOpen.set(false);
          this.loadProducts();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo crear el producto.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadProducts(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.productService
      .listProducts()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (products) => this.products.set(products),
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los productos.';
          this.errorMessage.set(backendError ?? fallbackError);
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

  protected trackVersion = (index: number): number => index;

  private createVersionGroup() {
    return this.formBuilder.nonNullable.group({
      name: ['', [Validators.required]],
    });
  }
}
