import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { provideIcons } from '@ng-icons/core';
import {
  lucideActivity,
  lucideAlertTriangle,
  lucideBarChart2,
  lucideCheckCircle,
  lucideDownload,
  lucideFilter,
  lucideInfo,
  lucidePackage,
  lucideRefreshCcw,
  lucideShieldAlert,
  lucideShoppingCart,
  lucideX,
} from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { finalize, take } from 'rxjs/operators';
import { AuthService } from '../../core/users/services/auth.service';
import {
  ActionMessage,
  ExcepcionMrp,
  ExportFormat,
  InventorySummary,
  KpiDashboard,
  OrdenSugerida,
} from './models/report.models';
import { ReportFilters, ReportService } from './services/report.service';

type TabId = 'dashboard' | 'inventario' | 'ordenes' | 'excepciones' | 'acciones';

interface ExportOption {
  label: string;
  format: ExportFormat;
  ext: string;
}

const EXPORT_OPTIONS: ExportOption[] = [
  { label: 'PDF', format: 'pdf', ext: '.pdf' },
  { label: 'Excel', format: 'excel', ext: '.xlsx' },
  { label: 'CSV', format: 'csv', ext: '.csv' },
];

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, ...HlmButtonImports, ...HlmIconImports],
  providers: [
    provideIcons({
      lucideRefreshCcw,
      lucideDownload,
      lucidePackage,
      lucideAlertTriangle,
      lucideShoppingCart,
      lucideActivity,
      lucideCheckCircle,
      lucideShieldAlert,
      lucideInfo,
      lucideBarChart2,
      lucideFilter,
      lucideX,
    }),
  ],
  templateUrl: './reports.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportsComponent implements OnInit {
  private readonly reportService = inject(ReportService);
  private readonly authService = inject(AuthService);

  protected readonly exportOptions = EXPORT_OPTIONS;
  protected readonly selectedExportFormat = signal<ExportFormat>('excel');
  /** Two-way ngModel binding proxy for the format <select> */
  protected exportFormatValue: ExportFormat = 'excel';

  protected readonly tabs: { id: TabId; label: string; icon: string }[] = [
    { id: 'dashboard', label: 'Dashboard', icon: 'lucideBarChart2' },
    { id: 'inventario', label: 'Inventario', icon: 'lucidePackage' },
    { id: 'ordenes', label: 'Órdenes Sugeridas', icon: 'lucideShoppingCart' },
    { id: 'excepciones', label: 'Excepciones', icon: 'lucideAlertTriangle' },
    { id: 'acciones', label: 'Acciones', icon: 'lucideActivity' },
  ];

  protected readonly activeTab = signal<TabId>('dashboard');

  // Dashboard / KPIs
  protected readonly dashboardLoading = signal(true);
  protected readonly dashboard = signal<KpiDashboard | null>(null);

  // Inventario
  protected readonly inventarioLoading = signal(false);
  protected readonly inventarioError = signal<string | null>(null);
  protected readonly inventario = signal<InventorySummary[]>([]);

  // Ordenes
  protected readonly ordenesLoading = signal(false);
  protected readonly ordenesError = signal<string | null>(null);
  protected readonly ordenes = signal<OrdenSugerida[]>([]);

  // Excepciones
  protected readonly excepcionesLoading = signal(false);
  protected readonly excepcionesError = signal<string | null>(null);
  protected readonly excepciones = signal<ExcepcionMrp[]>([]);

  // Acciones
  protected readonly accionesLoading = signal(false);
  protected readonly accionesError = signal<string | null>(null);
  protected readonly acciones = signal<ActionMessage[]>([]);

  // Export
  protected readonly exporting = signal(false);

  // Filters
  protected readonly showFilters = signal(false);
  protected readonly filters = signal<ReportFilters>({});
  protected filterStartDate = '';
  protected filterEndDate = '';
  protected filterMaterialCode = '';
  protected filterStatus = '';
  protected filterSeverity = '';
  protected filterPriority = '';

  ngOnInit(): void {
    this.loadDashboard();
  }

  protected setActiveTab(tab: TabId): void {
    this.activeTab.set(tab);
    switch (tab) {
      case 'dashboard':
        if (!this.dashboard()) this.loadDashboard();
        break;
      case 'inventario':
        if (this.inventario().length === 0 && !this.inventarioLoading())
          this.loadInventario();
        break;
      case 'ordenes':
        if (this.ordenes().length === 0 && !this.ordenesLoading())
          this.loadOrdenes();
        break;
      case 'excepciones':
        if (this.excepciones().length === 0 && !this.excepcionesLoading())
          this.loadExcepciones();
        break;
      case 'acciones':
        if (this.acciones().length === 0 && !this.accionesLoading())
          this.loadAcciones();
        break;
    }
  }

  protected applyFilters(): void {
    this.filters.set({
      startDate: this.filterStartDate || undefined,
      endDate: this.filterEndDate || undefined,
      materialCode: this.filterMaterialCode || undefined,
      status: this.filterStatus || undefined,
      severity: this.filterSeverity || undefined,
      priority: this.filterPriority || undefined,
    });
    this.showFilters.set(false);
    this.reloadCurrentTab();
  }

  protected clearFilters(): void {
    this.filterStartDate = '';
    this.filterEndDate = '';
    this.filterMaterialCode = '';
    this.filterStatus = '';
    this.filterSeverity = '';
    this.filterPriority = '';
    this.filters.set({});
    this.reloadCurrentTab();
  }

  private reloadCurrentTab(): void {
    const tab = this.activeTab();
    if (tab === 'inventario') this.loadInventario();
    else if (tab === 'ordenes') this.loadOrdenes();
    else if (tab === 'excepciones') this.loadExcepciones();
    else if (tab === 'acciones') this.loadAcciones();
  }

  protected hasActiveFilters(): boolean {
    const f = this.filters();
    return !!(
      f.startDate ||
      f.endDate ||
      f.materialCode ||
      f.status ||
      f.severity ||
      f.priority
    );
  }

  protected loadDashboard(): void {
    this.dashboardLoading.set(true);
    this.reportService
      .getDashboard()
      .pipe(
        take(1),
        finalize(() => this.dashboardLoading.set(false)),
      )
      .subscribe({
        next: (data) => this.dashboard.set(data),
        error: (err: HttpErrorResponse) => this.handleError(err),
      });
  }

  protected loadInventario(): void {
    this.inventarioLoading.set(true);
    this.inventarioError.set(null);
    this.reportService
      .getInventario(this.filters())
      .pipe(
        take(1),
        finalize(() => this.inventarioLoading.set(false)),
      )
      .subscribe({
        next: (data) => this.inventario.set(data),
        error: (err: HttpErrorResponse) => {
          this.handleError(err);
          this.inventarioError.set(this.extractErrorMessage(err));
        },
      });
  }

  protected loadOrdenes(): void {
    this.ordenesLoading.set(true);
    this.ordenesError.set(null);
    this.reportService
      .getOrdenesSugeridas(this.filters())
      .pipe(
        take(1),
        finalize(() => this.ordenesLoading.set(false)),
      )
      .subscribe({
        next: (data) => this.ordenes.set(data),
        error: (err: HttpErrorResponse) => {
          this.handleError(err);
          this.ordenesError.set(this.extractErrorMessage(err));
        },
      });
  }

  protected loadExcepciones(): void {
    this.excepcionesLoading.set(true);
    this.excepcionesError.set(null);
    this.reportService
      .getExcepciones(this.filters())
      .pipe(
        take(1),
        finalize(() => this.excepcionesLoading.set(false)),
      )
      .subscribe({
        next: (data) => this.excepciones.set(data),
        error: (err: HttpErrorResponse) => {
          this.handleError(err);
          this.excepcionesError.set(this.extractErrorMessage(err));
        },
      });
  }

  protected loadAcciones(): void {
    this.accionesLoading.set(true);
    this.accionesError.set(null);
    this.reportService
      .getAcciones(this.filters())
      .pipe(
        take(1),
        finalize(() => this.accionesLoading.set(false)),
      )
      .subscribe({
        next: (data) => this.acciones.set(data),
        error: (err: HttpErrorResponse) => {
          this.handleError(err);
          this.accionesError.set(this.extractErrorMessage(err));
        },
      });
  }

  protected exportReport(
    type: 'dashboard' | 'inventario' | 'ordenes' | 'excepciones' | 'acciones',
  ): void {
    this.exporting.set(true);
    const fmt = this.exportFormatValue;
    this.selectedExportFormat.set(fmt);
    const filters = this.filters();

    const exportFileNames: Record<string, string> = {
      dashboard: `kpi-dashboard${this.extFor(fmt)}`,
      inventario: `inventario${this.extFor(fmt)}`,
      ordenes: `ordenes-sugeridas${this.extFor(fmt)}`,
      excepciones: `excepciones${this.extFor(fmt)}`,
      acciones: `acciones${this.extFor(fmt)}`,
    };

    const exportCalls: Record<string, () => import('rxjs').Observable<Blob>> = {
      dashboard: () => this.reportService.exportDashboard(fmt),
      inventario: () => this.reportService.exportInventario(fmt, filters),
      ordenes: () => this.reportService.exportOrdenesSugeridas(fmt, filters),
      excepciones: () => this.reportService.exportExcepciones(fmt, filters),
      acciones: () => this.reportService.exportAcciones(fmt, filters),
    };

    exportCalls[type]()
      .pipe(
        take(1),
        finalize(() => this.exporting.set(false)),
      )
      .subscribe({
        next: (blob) => this.downloadBlob(blob, exportFileNames[type]),
        error: (err: HttpErrorResponse) => {
          this.handleError(err);
        },
      });
  }

  private extFor(format: ExportFormat): string {
    return format === 'pdf' ? '.pdf' : format === 'csv' ? '.csv' : '.xlsx';
  }

  protected formatNumber(value: number | null | undefined): string {
    if (value == null) return '—';
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2,
    }).format(value);
  }

  protected formatCurrency(value: number | null | undefined): string {
    if (value == null) return '—';
    return new Intl.NumberFormat('es-VE', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
    }).format(value);
  }

  protected getEstadoClass(estado: string): string {
    switch (estado) {
      case 'CRITICO': return 'bg-red-500/10 text-red-700';
      case 'BAJO': return 'bg-amber-500/10 text-amber-700';
      case 'EXCESO': return 'bg-purple-500/10 text-purple-700';
      default: return 'bg-emerald-500/10 text-emerald-700';
    }
  }

  protected getSeveridadBorderClass(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'border-red-500/30';
      case 'WARNING': return 'border-amber-500/30';
      default: return 'border-blue-500/30';
    }
  }

  protected getSeveridadIconClass(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'bg-red-500/10 text-red-600';
      case 'WARNING': return 'bg-amber-500/10 text-amber-600';
      default: return 'bg-blue-500/10 text-blue-600';
    }
  }

  protected getSeveridadIcon(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'lucideShieldAlert';
      case 'WARNING': return 'lucideAlertTriangle';
      default: return 'lucideInfo';
    }
  }

  protected getSeveridadBadgeClass(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'bg-red-500/10 text-red-700';
      case 'WARNING': return 'bg-amber-500/10 text-amber-700';
      default: return 'bg-blue-500/10 text-blue-700';
    }
  }

  protected getPrioridadClass(prioridad: string): string {
    switch (prioridad) {
      case 'HIGH': return 'bg-red-500/10 text-red-700';
      case 'MEDIUM': return 'bg-amber-500/10 text-amber-700';
      default: return 'bg-emerald-500/10 text-emerald-700';
    }
  }

  private downloadBlob(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  private handleError(err: HttpErrorResponse): void {
    if (err.status === 403) {
      this.authService.logout();
    }
  }

  private extractErrorMessage(err: HttpErrorResponse): string {
    return (
      err?.error?.message ??
      err?.error?.error ??
      'Error al cargar los datos.'
    );
  }
}
