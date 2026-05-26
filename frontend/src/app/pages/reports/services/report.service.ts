import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { apiEndpoint } from '../../../core/config/api.config';
import { API_ROUTES } from '../../../core/config/api-routes.utils';
import {
  ActionMessage,
  DashboardSummary,
  ExcepcionMrp,
  InventorySummary,
  OrdenSugerida,
} from '../models/report.models';

@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private readonly http = inject(HttpClient);

  getDashboard(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(apiEndpoint(API_ROUTES.reports.dashboard));
  }

  getInventario(): Observable<InventorySummary[]> {
    return this.http.get<InventorySummary[]>(apiEndpoint(API_ROUTES.reports.inventario));
  }

  getOrdenesSugeridas(): Observable<OrdenSugerida[]> {
    return this.http.get<OrdenSugerida[]>(apiEndpoint(API_ROUTES.reports.ordenesSugeridas));
  }

  getExcepciones(): Observable<ExcepcionMrp[]> {
    return this.http.get<ExcepcionMrp[]>(apiEndpoint(API_ROUTES.reports.excepciones));
  }

  getAcciones(): Observable<ActionMessage[]> {
    return this.http.get<ActionMessage[]>(apiEndpoint(API_ROUTES.reports.acciones));
  }

  exportInventarioExcel(): Observable<Blob> {
    return this.http.get(apiEndpoint(API_ROUTES.reports.inventarioExcel), {
      responseType: 'blob',
    });
  }

  exportOrdenesSugeridasExcel(): Observable<Blob> {
    return this.http.get(apiEndpoint(API_ROUTES.reports.ordenesSugeridasExcel), {
      responseType: 'blob',
    });
  }

  exportExcepcionesExcel(): Observable<Blob> {
    return this.http.get(apiEndpoint(API_ROUTES.reports.excepcionesExcel), {
      responseType: 'blob',
    });
  }

  exportAccionesExcel(): Observable<Blob> {
    return this.http.get(apiEndpoint(API_ROUTES.reports.accionesExcel), {
      responseType: 'blob',
    });
  }
}
