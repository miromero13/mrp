import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiEndpoint } from '../../../core/config/api.config';
import { API_ROUTES } from '../../../core/config/api-routes.utils';
import {
  ActionMessage,
  ExportFormat,
  ExcepcionMrp,
  InventorySummary,
  KpiDashboard,
  OrdenSugerida,
} from '../models/report.models';

/** Filtros opcionales para los reportes analíticos */
export interface ReportFilters {
  startDate?: string;   // ISO date yyyy-MM-dd
  endDate?: string;
  materialCode?: string;
  priority?: string;
  severity?: string;
  status?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private readonly http = inject(HttpClient);

  // ── Gerencial ────────────────────────────────────────────────────────────────

  getDashboard(): Observable<KpiDashboard> {
    return this.http.get<KpiDashboard>(apiEndpoint(API_ROUTES.reports.dashboard));
  }

  exportDashboard(format: ExportFormat = 'pdf'): Observable<Blob> {
    const params = new HttpParams().set('format', format);
    return this.http.get(apiEndpoint(API_ROUTES.reports.dashboardExport), {
      params,
      responseType: 'blob',
    });
  }

  // ── Analítico — JSON ─────────────────────────────────────────────────────────

  getInventario(filters?: ReportFilters): Observable<InventorySummary[]> {
    const params = this.buildParams(filters);
    return this.http.get<InventorySummary[]>(apiEndpoint(API_ROUTES.reports.inventario), { params });
  }

  getOrdenesSugeridas(filters?: ReportFilters): Observable<OrdenSugerida[]> {
    const params = this.buildParams(filters);
    return this.http.get<OrdenSugerida[]>(apiEndpoint(API_ROUTES.reports.ordenesSugeridas), { params });
  }

  getExcepciones(filters?: ReportFilters): Observable<ExcepcionMrp[]> {
    const params = this.buildParams(filters);
    return this.http.get<ExcepcionMrp[]>(apiEndpoint(API_ROUTES.reports.excepciones), { params });
  }

  getAcciones(filters?: ReportFilters): Observable<ActionMessage[]> {
    const params = this.buildParams(filters);
    return this.http.get<ActionMessage[]>(apiEndpoint(API_ROUTES.reports.acciones), { params });
  }

  // ── Analítico — Exportación ──────────────────────────────────────────────────

  exportInventario(format: ExportFormat = 'excel', filters?: ReportFilters): Observable<Blob> {
    const params = this.buildParams(filters).set('format', format);
    return this.http.get(apiEndpoint(API_ROUTES.reports.inventarioExport), {
      params,
      responseType: 'blob',
    });
  }

  exportOrdenesSugeridas(format: ExportFormat = 'excel', filters?: ReportFilters): Observable<Blob> {
    const params = this.buildParams(filters).set('format', format);
    return this.http.get(apiEndpoint(API_ROUTES.reports.ordenesSugeridasExport), {
      params,
      responseType: 'blob',
    });
  }

  exportExcepciones(format: ExportFormat = 'excel', filters?: ReportFilters): Observable<Blob> {
    const params = this.buildParams(filters).set('format', format);
    return this.http.get(apiEndpoint(API_ROUTES.reports.excepcionesExport), {
      params,
      responseType: 'blob',
    });
  }

  exportAcciones(format: ExportFormat = 'excel', filters?: ReportFilters): Observable<Blob> {
    const params = this.buildParams(filters).set('format', format);
    return this.http.get(apiEndpoint(API_ROUTES.reports.accionesExport), {
      params,
      responseType: 'blob',
    });
  }

  // ── Helper ───────────────────────────────────────────────────────────────────

  private buildParams(filters?: ReportFilters): HttpParams {
    let params = new HttpParams();
    if (!filters) return params;
    if (filters.startDate)    params = params.set('startDate', filters.startDate);
    if (filters.endDate)      params = params.set('endDate', filters.endDate);
    if (filters.materialCode) params = params.set('materialCode', filters.materialCode);
    if (filters.priority)     params = params.set('priority', filters.priority);
    if (filters.severity)     params = params.set('severity', filters.severity);
    if (filters.status)       params = params.set('status', filters.status);
    return params;
  }
}
