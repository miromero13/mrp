import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucideEye, lucideMinus, lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { PERMISOS } from '../../../core/config/permisos';
import { AuthService } from '../../../core/users/services/auth.service';
import { ProductService } from '../../../core/enterprises/services/product.service';
import { MachineService } from '../../../core/enterprises/services/machine.service';
import { ProductionRouteService } from '../../../core/enterprises/services/production-route.service';
import { MachineListItem } from '../../../core/enterprises/models/machine.models';
import { ProductListItem } from '../../../core/enterprises/models/product.models';
import { CreateProductionRouteFormValue, ProductionRouteListItem, RouteOperationFormValue } from '../../../core/enterprises/models/production-route.models';

@Component({
  selector: 'app-production-routes',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  providers: [provideIcons({ lucideEye, lucideMinus, lucidePencil, lucidePlus, lucideTrash2, lucideX })],
  templateUrl: './production-routes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductionRoutesComponent implements OnInit {
  @ViewChild('actionsTemplate', { static: true })
  private readonly actionsTemplate!: TemplateRef<{ $implicit: ProductionRouteListItem }>;

  private readonly formBuilder = inject(FormBuilder);
  private readonly productionRouteService = inject(ProductionRouteService);
  private readonly productService = inject(ProductService);
  private readonly machineService = inject(MachineService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly loadingProducts = signal(true);
  protected readonly loadingMachines = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formErrorMessage = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly modalMode = signal<'create' | 'view' | 'edit'>('create');
  protected readonly editingRouteId = signal<string | null>(null);
  protected readonly selectedFilterVersionId = signal<string>('');
  protected readonly routes = signal<ProductionRouteListItem[]>([]);
  protected readonly products = signal<ProductListItem[]>([]);
  protected readonly machines = signal<MachineListItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canViewRoute = computed(() => this.adminPermissionNames().has(PERMISOS.productionRoutes.listar));
  protected readonly canCreateRoute = computed(() => this.adminPermissionNames().has(PERMISOS.productionRoutes.crear));
  protected readonly canEditRoute = computed(() => this.adminPermissionNames().has(PERMISOS.productionRoutes.editar));
  protected readonly canDeleteRoute = computed(() => this.adminPermissionNames().has(PERMISOS.productionRoutes.eliminar));
  protected readonly isReadOnly = computed(() => this.modalMode() === 'view');

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

  protected readonly filteredRoutes = computed(() => {
    const versionId = this.selectedFilterVersionId();
    return versionId ? this.routes().filter((route) => route.productVersionId === versionId) : this.routes();
  });

  protected readonly form = this.formBuilder.nonNullable.group({
    productVersionId: ['', [Validators.required]],
    name: ['', [Validators.required]],
    isActive: [true],
    operations: this.formBuilder.array([this.createOperationGroup()]),
  });

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<ProductionRouteListItem>>>(() => [
    { id: 'product', header: 'Producto', cell: (route) => `${route.productName} - ${route.productVersionName}` },
    { id: 'name', header: 'Ruta', cell: (route) => route.name },
    { id: 'isActive', header: 'Activa', cell: (route) => (route.isActive ? 'Sí' : 'No') },
    { id: 'operations', header: 'Pasos', cell: (route) => `${route.operations.length}` },
    {
      id: 'summary',
      header: 'Resumen',
      cell: (route) => route.operations.map((operation) => `#${operation.operationOrder} ${operation.description}`).join(' · ') || '-',
    },
    {
      id: 'actions',
      header: 'Acciones',
      cell: () => '',
      template: this.actionsTemplate,
      align: 'right',
    },
  ]);

  get operations(): FormArray {
    return this.form.controls.operations;
  }

  get operationGroups(): FormGroup[] {
    return this.operations.controls as FormGroup[];
  }

  ngOnInit(): void {
    this.loadProducts();
    this.loadMachines();
    this.loadRoutes();
  }

  protected openCreateModal(): void {
    if (!this.canCreateRoute() || this.versionOptions().length === 0) {
      return;
    }

    const defaultVersionId = this.selectedFilterVersionId() || this.versionOptions()[0]?.id || '';
    this.formErrorMessage.set(null);
    this.modalMode.set('create');
    this.editingRouteId.set(null);
    this.operations.clear();
    this.operations.push(this.createOperationGroup());
    this.form.reset({
      productVersionId: defaultVersionId,
      name: '',
      isActive: true,
    });
    this.form.patchValue({ productVersionId: defaultVersionId });
    this.isModalOpen.set(true);
  }

  protected openRouteModal(route: ProductionRouteListItem, mode: 'view' | 'edit'): void {
    if (mode === 'view' && !this.canViewRoute()) {
      return;
    }
    if (mode === 'edit' && !this.canEditRoute()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set(mode);
    this.editingRouteId.set(route.id);
    this.operations.clear();
    for (const operation of route.operations) {
      this.operations.push(this.createOperationGroup(operation));
    }

    this.form.reset({
      productVersionId: route.productVersionId,
      name: route.name,
      isActive: route.isActive,
    });
    this.form.patchValue({ productVersionId: route.productVersionId });
    this.isModalOpen.set(true);
  }

  protected closeModal(): void {
    if (this.saving()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.isModalOpen.set(false);
    this.editingRouteId.set(null);
    this.modalMode.set('create');
  }

  protected addOperation(): void {
    if (this.isReadOnly()) {
      return;
    }

    this.operations.push(this.createOperationGroup());
  }

  protected removeOperation(index: number): void {
    if (this.isReadOnly()) {
      return;
    }

    if (this.operations.length > 1) {
      this.operations.removeAt(index);
      return;
    }

    this.operations.at(0)?.reset({ machineId: null, operationOrder: 1, description: '', setupTimeMinutes: 0, runTimeMinutes: 0 });
  }

  protected saveRoute(): void {
    if (this.form.invalid || this.operations.length === 0) {
      this.form.markAllAsTouched();
      return;
    }

    const rawValue = this.form.getRawValue();
    const payload: CreateProductionRouteFormValue = {
      productVersionId: rawValue.productVersionId,
      name: rawValue.name.trim(),
      isActive: Boolean(rawValue.isActive),
      operations: rawValue.operations.map((operation) => ({
        machineId: operation.machineId || null,
        operationOrder: Number(operation.operationOrder),
        description: String(operation.description ?? '').trim(),
        setupTimeMinutes: Number(operation.setupTimeMinutes),
        runTimeMinutes: Number(operation.runTimeMinutes),
      })),
    };

    this.saving.set(true);
    this.formErrorMessage.set(null);

    const request$ = this.modalMode() === 'edit' && this.editingRouteId()
      ? this.productionRouteService.updateProductionRoute(this.editingRouteId() as string, payload)
      : this.productionRouteService.createProductionRoute(payload);

    request$
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isModalOpen.set(false);
          this.editingRouteId.set(null);
          this.loadRoutes();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo guardar la ruta.';
          this.formErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deleteRoute(route: ProductionRouteListItem): void {
    if (!this.canDeleteRoute() || !confirm('¿Eliminar esta ruta?')) {
      return;
    }

    this.productionRouteService.deleteProductionRoute(route.id).pipe(take(1)).subscribe({
      next: () => this.loadRoutes(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar la ruta.';
        this.errorMessage.set(backendError ?? fallbackError);
      },
    });
  }

  protected onVersionFilterChange(versionId: string | null): void {
    const safeVersionId = versionId ?? '';
    this.selectedFilterVersionId.set(safeVersionId);
    this.loadRoutes(safeVersionId || undefined);
  }

  protected onRouteVersionChange(versionId: string | null): void {
    this.form.controls.productVersionId.setValue(versionId ?? '');
  }

  protected onOperationMachineChange(index: number, machineId: string | null): void {
    this.operations.at(index)?.get('machineId')?.setValue(machineId ?? '');
  }

  protected loadRoutes(versionId?: string): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.productionRouteService
      .listProductionRoutes(versionId)
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (routes) => this.routes.set(routes),
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar las rutas.';
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
          if (!this.selectedFilterVersionId() && products.length > 0 && products[0].versions.length > 0) {
            this.selectedFilterVersionId.set('');
          }
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los productos.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadMachines(): void {
    this.loadingMachines.set(true);

    this.machineService
      .listMachines()
      .pipe(
        take(1),
        finalize(() => this.loadingMachines.set(false)),
      )
      .subscribe({
        next: (machines) => this.machines.set(machines),
        error: () => this.machines.set([]),
      });
  }

  protected trackOperation = (index: number): number => index;

  protected filterVersionLabel(versionId: string | null): string {
    if (!versionId) {
      return 'Todas';
    }

    return this.versionOptions().find((version) => version.id === versionId)?.label ?? 'Todas';
  }

  protected routeVersionLabel(versionId: string | null): string {
    if (!versionId) {
      return 'Selecciona una versión';
    }

    return this.versionOptions().find((version) => version.id === versionId)?.label ?? 'Selecciona una versión';
  }

  protected machineLabel(machineId: string | null): string {
    if (!machineId) {
      return 'Sin máquina';
    }

    const machine = this.machines().find((item) => item.id === machineId);
    return machine ? `${machine.name} - ${machine.serialNumber}` : 'Sin máquina';
  }

  private createOperationGroup(operation?: Partial<RouteOperationFormValue>) {
    return this.formBuilder.group({
      machineId: [operation?.machineId ?? null],
      operationOrder: [operation?.operationOrder ?? 1, [Validators.required, Validators.min(1)]],
      description: [operation?.description ?? '', [Validators.required]],
      setupTimeMinutes: [operation?.setupTimeMinutes ?? 0, [Validators.required, Validators.min(0)]],
      runTimeMinutes: [operation?.runTimeMinutes ?? 0, [Validators.required, Validators.min(0)]],
    });
  }
}
