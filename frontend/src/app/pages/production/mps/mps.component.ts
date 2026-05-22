import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucideBuilding2, lucideClipboardClock, lucidePackage, lucideRefreshCw, lucideSave } from '@ng-icons/lucide';
import { BrnTabsImports } from '@spartan-ng/brain/tabs';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSelectImports } from '@spartan-ng/helm/select';
import { AuthService } from '../../../core/users/services/auth.service';
import { ProductService } from '../../../core/enterprises/services/product.service';
import { ProductListItem } from '../../../core/enterprises/models/product.models';
import { GenerateMpsFormValue, MpsPeriodMode, MpsRecordListItem } from '../../../core/production/models/mps.models';
import { MpsService } from '../../../core/production/services/mps.service';

interface MpsBoardRow extends MpsRecordListItem {
  openingBalance: number;
  totalDemand: number;
  suggestedProduction: number;
  savedPlannedProduction: number;
  isDirty: boolean;
  periodLabel: string;
}

interface MpsSummary {
  forecastDemand: number;
  allocatedDemand: number;
  totalDemand: number;
  plannedProduction: number;
  projectedAvailableBalance: number;
}

@Component({
  selector: 'app-mps',
  standalone: true,
  imports: [CommonModule, ...BrnTabsImports, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSelectImports],
  providers: [provideIcons({ lucideBuilding2, lucideClipboardClock, lucidePackage, lucideRefreshCw, lucideSave })],
  templateUrl: './mps.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MpsComponent {
  private readonly authService = inject(AuthService);
  private readonly productService = inject(ProductService);
  private readonly mpsService = inject(MpsService);

  protected readonly loadingProducts = signal(false);
  protected readonly loadingBoard = signal(false);
  protected readonly generating = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly savingRowId = signal<string | null>(null);
  protected readonly products = signal<ProductListItem[]>([]);
  protected readonly records = signal<MpsRecordListItem[]>([]);
  protected readonly savedPlannedProductionById = signal<Record<string, number>>({});
  protected readonly selectedProductId = signal<string | null>(null);
  protected readonly periodMode = signal<MpsPeriodMode>(MpsPeriodMode.WEEKLY);

  protected readonly currentEnterprise = computed(() => this.authService.currentUser()?.enterprise ?? null);

  protected readonly productOptions = computed(() => this.products().map((product) => ({ id: product.id, label: product.name })));

  protected readonly boardRows = computed<MpsBoardRow[]>(() => {
    let openingBalance = 0;

    return this.records().map((record) => {
      const totalDemand = this.round(record.forecastDemand + record.allocatedDemand);
      const suggestedProduction = this.round(Math.max(0, totalDemand - openingBalance));
      const plannedProduction = this.round(record.plannedProduction);
      const projectedAvailableBalance = this.round(openingBalance + plannedProduction - totalDemand);
      const savedPlannedProduction = this.round(this.savedPlannedProductionById()[record.id] ?? plannedProduction);
      const row: MpsBoardRow = {
        ...record,
        openingBalance: this.round(openingBalance),
        totalDemand,
        suggestedProduction,
        savedPlannedProduction,
        isDirty: this.round(plannedProduction) !== savedPlannedProduction,
        projectedAvailableBalance,
        periodLabel: this.periodLabel(record.periodStartDate, record.periodMode),
      };

      openingBalance = projectedAvailableBalance;
      return row;
    });
  });

  protected readonly summary = computed<MpsSummary>(() => {
    const rows = this.boardRows();
    return rows.reduce<MpsSummary>(
      (acc, row) => ({
        forecastDemand: this.round(acc.forecastDemand + row.forecastDemand),
        allocatedDemand: this.round(acc.allocatedDemand + row.allocatedDemand),
        totalDemand: this.round(acc.totalDemand + row.totalDemand),
        plannedProduction: this.round(acc.plannedProduction + row.plannedProduction),
        projectedAvailableBalance: row.projectedAvailableBalance,
      }),
      { forecastDemand: 0, allocatedDemand: 0, totalDemand: 0, plannedProduction: 0, projectedAvailableBalance: 0 },
    );
  });

  ngOnInit(): void {
    const enterprise = this.currentEnterprise();
    if (!enterprise) {
      this.errorMessage.set('No tienes una empresa asignada.');
      return;
    }

    this.loadProducts(enterprise.id);
  }

  protected setPeriodMode(mode: string | undefined): void {
    if (mode !== MpsPeriodMode.WEEKLY && mode !== MpsPeriodMode.MONTHLY) {
      return;
    }

    this.periodMode.set(mode);
    this.loadBoard();
  }

  protected onEnterpriseChange(enterpriseId: string | null): void {
    return;
  }

  protected onProductChange(productId: string | null): void {
    this.selectedProductId.set(productId);
    this.loadBoard();
  }

  protected generateBoard(): void {
    const enterpriseId = this.currentEnterprise()?.id;
    const productId = this.selectedProductId();

    if (!enterpriseId || !productId) {
      return;
    }

    this.generating.set(true);
    this.errorMessage.set(null);

    const payload: GenerateMpsFormValue = {
      enterpriseId,
      productId,
      periodMode: this.periodMode(),
      periodCount: this.defaultPeriodCount(),
    };

    this.mpsService
      .generateMps(payload)
      .pipe(finalize(() => this.generating.set(false)), take(1))
      .subscribe({
        next: (records) => this.applyRecords(records),
        error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudo generar el MPS.'),
      });
  }

  protected saveRow(row: MpsBoardRow): void {
    if (!row.isDirty || this.savingRowId()) {
      return;
    }

    this.savingRowId.set(row.id);
    this.errorMessage.set(null);

    this.mpsService
      .updateMpsRecord(row.id, { plannedProduction: row.plannedProduction })
      .pipe(finalize(() => this.savingRowId.set(null)), take(1))
      .subscribe({
        next: () => this.loadBoard(),
        error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudo actualizar la sugerencia.'),
      });
  }

  protected onPlannedProductionInput(rowId: string, value: string | number | null): void {
    const plannedProduction = this.normalizeInputValue(value);
    this.records.update((rows) => rows.map((row) => (row.id === rowId ? { ...row, plannedProduction } : row)));
  }

  protected loadBoard(): void {
    const enterpriseId = this.currentEnterprise()?.id;
    const productId = this.selectedProductId();

    if (!enterpriseId || !productId) {
      this.records.set([]);
      this.savedPlannedProductionById.set({});
      return;
    }

    this.loadingBoard.set(true);
    this.errorMessage.set(null);

    this.mpsService
      .listMps(productId, this.periodMode())
      .pipe(finalize(() => this.loadingBoard.set(false)), take(1))
      .subscribe({
        next: (records) => this.applyRecords(records),
        error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudo cargar el tablero MPS.'),
      });
  }

  protected trackRow = (_index: number, row: MpsBoardRow): string => row.id;

  protected formatNumber(value: number): string {
    return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
  }

  private loadProducts(enterpriseId: string): void {
    this.loadingProducts.set(true);

    this.productService
      .listProducts(enterpriseId)
      .pipe(finalize(() => this.loadingProducts.set(false)), take(1))
      .subscribe({
        next: (products) => {
          this.products.set(products);
          const selectedProductId = this.selectedProductId() ?? products[0]?.id ?? null;
          this.selectedProductId.set(selectedProductId);

          if (selectedProductId) {
            this.loadBoard();
          } else {
            this.records.set([]);
            this.savedPlannedProductionById.set({});
          }
        },
        error: (error: HttpErrorResponse) => this.handleError(error, 'No se pudieron cargar los productos.'),
      });
  }

  private applyRecords(records: MpsRecordListItem[]): void {
    this.records.set(records);
    this.savedPlannedProductionById.set(Object.fromEntries(records.map((record) => [record.id, this.round(record.plannedProduction)])));
  }

  private handleError(error: HttpErrorResponse, fallbackMessage: string): void {
    const backendError = error?.error?.error ?? error?.error?.message;
    const fallbackError = error?.message ?? fallbackMessage;
    this.errorMessage.set(backendError ?? fallbackError);
  }

  private defaultPeriodCount(): number {
    return this.periodMode() === MpsPeriodMode.MONTHLY ? 6 : 12;
  }

  private normalizeInputValue(value: string | number | null): number {
    const normalized = typeof value === 'number' ? value : Number(value ?? 0);
    return this.round(Number.isFinite(normalized) ? normalized : 0);
  }

  private round(value: number): number {
    return Number(value.toFixed(2));
  }

  private periodLabel(periodStartDate: string, periodMode: MpsPeriodMode): string {
    const start = new Date(`${periodStartDate}T00:00:00`);

    if (periodMode === MpsPeriodMode.MONTHLY) {
      return start.toLocaleDateString('es-CO', { month: 'long', year: 'numeric' });
    }

    const end = new Date(start);
    end.setDate(start.getDate() + 6);
    return `${start.toLocaleDateString('es-CO', { day: '2-digit', month: 'short' })} - ${end.toLocaleDateString('es-CO', { day: '2-digit', month: 'short' })}`;
  }
}
