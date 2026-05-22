import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { BrnTabsImports } from '@spartan-ng/brain/tabs';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { PERMISOS } from '../../../core/config/permisos';
import { AuthService } from '../../../core/users/services/auth.service';
import { ProductService } from '../../../core/enterprises/services/product.service';
import { DemandService } from '../../../core/production/services/demand.service';
import { DemandFormValue, DemandListItem, DemandStatus, DemandType } from '../../../core/production/models/demand.models';
import { ProductListItem } from '../../../core/enterprises/models/product.models';

type PeriodMode = 'weekly' | 'monthly';

interface DemandGroup {
  label: string;
  items: DemandListItem[];
}

@Component({
  selector: 'app-demands',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...BrnTabsImports, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  providers: [provideIcons({ lucidePencil, lucidePlus, lucideTrash2, lucideX })],
  templateUrl: './demands.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DemandsComponent implements OnInit {
  @ViewChild('actionsTemplate', { static: true })
  private readonly actionsTemplate!: TemplateRef<{ $implicit: DemandListItem }>;

  private readonly formBuilder = inject(FormBuilder);
  private readonly demandService = inject(DemandService);
  private readonly productService = inject(ProductService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formErrorMessage = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly modalMode = signal<'create' | 'edit'>('create');
  protected readonly editingDemandId = signal<string | null>(null);
  protected readonly demands = signal<DemandListItem[]>([]);
  protected readonly products = signal<ProductListItem[]>([]);
  protected readonly periodMode = signal<PeriodMode>('weekly');

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canViewDemand = computed(() => this.adminPermissionNames().has(PERMISOS.production.demands.listar));
  protected readonly canCreateDemand = computed(() => this.adminPermissionNames().has(PERMISOS.production.demands.crear));
  protected readonly canEditDemand = computed(() => this.adminPermissionNames().has(PERMISOS.production.demands.editar));
  protected readonly canDeleteDemand = computed(() => this.adminPermissionNames().has(PERMISOS.production.demands.eliminar));
  protected readonly isReadOnly = computed(() => false);

  protected readonly demandGroups = computed<DemandGroup[]>(() => {
    const groups = new Map<string, DemandListItem[]>();

    for (const demand of this.demands()) {
      const date = new Date(`${demand.dueDate}T00:00:00`);
      const key = this.periodMode() === 'monthly' ? this.monthKey(date) : this.weekKey(date);
      const list = groups.get(key) ?? [];
      list.push(demand);
      groups.set(key, list);
    }

    return Array.from(groups.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([key, items]) => ({ label: this.periodMode() === 'monthly' ? this.monthLabel(key) : this.weekLabel(key), items }));
  });

  protected readonly demandColumns = computed<ReadonlyArray<CustomTableColumn<DemandListItem>>>(() => [
    { id: 'productName', header: 'Producto', cell: (demand) => demand.productName },
    { id: 'type', header: 'Tipo', cell: (demand) => this.typeLabel(demand.type) },
    { id: 'quantity', header: 'Cantidad', cell: (demand) => String(demand.quantity) },
    { id: 'dueDate', header: 'Entrega', cell: (demand) => this.formatDate(demand.dueDate) },
    { id: 'status', header: 'Estado', cell: (demand) => this.statusLabel(demand.status) },
    {
      id: 'actions',
      header: 'Acciones',
      cell: () => '',
      template: this.actionsTemplate,
      align: 'right',
    },
  ]);

  protected readonly form = this.formBuilder.nonNullable.group({
    productId: ['', [Validators.required]],
    type: [DemandType.SALE_ORDER, [Validators.required]],
    quantity: [0, [Validators.required, Validators.min(0.01)]],
    dueDate: ['', [Validators.required]],
    status: [DemandStatus.PENDING, [Validators.required]],
  });

  protected readonly productOptions = computed(() =>
    this.products().map((product) => ({ id: product.id, label: product.name })),
  );

  protected readonly selectedProductLabel = computed(() => {
    const productId = this.form.controls.productId.value;
    return this.productOptions().find((product) => product.id === productId)?.label ?? 'Selecciona un producto';
  });

  protected readonly selectedTypeLabel = computed(() => this.typeLabel(this.form.controls.type.value));
  protected readonly selectedStatusLabel = computed(() => this.statusLabel(this.form.controls.status.value));

  ngOnInit(): void {
    this.loadProducts();
    this.loadDemands();
  }

  protected setPeriodMode(mode: string | undefined): void {
    if (mode === 'weekly' || mode === 'monthly') {
      this.periodMode.set(mode);
    }
  }

  protected openCreateModal(): void {
    if (!this.canCreateDemand() || this.productOptions().length === 0) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set('create');
    this.editingDemandId.set(null);
    this.form.reset({
      productId: this.productOptions()[0]?.id ?? '',
      type: DemandType.SALE_ORDER,
      quantity: 0,
      dueDate: '',
      status: DemandStatus.PENDING,
    });
    this.isModalOpen.set(true);
  }

  protected openEditModal(demand: DemandListItem): void {
    if (!this.canEditDemand()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.modalMode.set('edit');
    this.editingDemandId.set(demand.id);
    this.form.reset({
      productId: demand.productId,
      type: demand.type,
      quantity: demand.quantity,
      dueDate: demand.dueDate,
      status: demand.status,
    });
    this.isModalOpen.set(true);
  }

  protected closeModal(): void {
    if (this.saving()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.isModalOpen.set(false);
    this.editingDemandId.set(null);
    this.modalMode.set('create');
  }

  protected saveDemand(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: DemandFormValue = {
      productId: value.productId,
      type: value.type,
      quantity: Number(value.quantity),
      dueDate: value.dueDate,
      status: value.status,
    };

    this.saving.set(true);
    this.formErrorMessage.set(null);

    const request$ = this.modalMode() === 'edit' && this.editingDemandId()
      ? this.demandService.updateDemand(this.editingDemandId() as string, payload)
      : this.demandService.createDemand(payload);

    request$
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isModalOpen.set(false);
          this.editingDemandId.set(null);
          this.loadDemands();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo guardar la demanda.';
          this.formErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected cancelDemand(demand: DemandListItem): void {
    if (!this.canDeleteDemand() || !confirm('¿Cancelar esta demanda?')) {
      return;
    }

    this.demandService.cancelDemand(demand.id).pipe(take(1)).subscribe({
      next: () => this.loadDemands(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo cancelar la demanda.';
        this.errorMessage.set(backendError ?? fallbackError);
      },
    });
  }

  protected loadDemands(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.demandService
      .listDemands()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (demands) => this.demands.set(demands),
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar las demandas.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadProducts(): void {
    this.productService
      .listProducts()
      .pipe(take(1))
      .subscribe({
        next: (products) => this.products.set(products),
        error: () => this.products.set([]),
      });
  }

  protected onProductChange(productId: string | null): void {
    this.form.controls.productId.setValue(productId ?? '');
  }

  protected onTypeChange(type: string | null): void {
    this.form.controls.type.setValue((type ?? DemandType.SALE_ORDER) as DemandType);
  }

  protected onStatusChange(status: string | null): void {
    this.form.controls.status.setValue((status ?? DemandStatus.PENDING) as DemandStatus);
  }

  protected typeLabel(type: DemandType): string {
    return type === DemandType.FORECAST ? 'Pronóstico' : 'Pedido real';
  }

  protected statusLabel(status: DemandStatus): string {
    switch (status) {
      case DemandStatus.FULFILLED:
        return 'Cumplida';
      case DemandStatus.CANCELLED:
        return 'Cancelada';
      default:
        return 'Pendiente';
    }
  }

  protected formatDate(value: string): string {
    return new Date(`${value}T00:00:00`).toLocaleDateString('es-CO');
  }

  protected trackDemand = (index: number): number => index;

  private weekKey(date: Date): string {
    const copy = new Date(date);
    const day = copy.getDay() || 7;
    copy.setDate(copy.getDate() - day + 1);
    return copy.toISOString().slice(0, 10);
  }

  private monthKey(date: Date): string {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
  }

  private weekLabel(weekStart: string): string {
    const date = new Date(`${weekStart}T00:00:00`);
    const end = new Date(date);
    end.setDate(date.getDate() + 6);
    return `Semana ${date.toLocaleDateString('es-CO')} - ${end.toLocaleDateString('es-CO')}`;
  }

  private monthLabel(monthKey: string): string {
    const [year, month] = monthKey.split('-').map(Number);
    return new Date(year, month - 1, 1).toLocaleDateString('es-CO', { month: 'long', year: 'numeric' });
  }
}
