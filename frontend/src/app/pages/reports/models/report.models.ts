export interface InventorySummary {
  codigo: string;
  nombre: string;
  unidadMedida: string;
  stockActual: number;
  stockMinimo: number;
  comprometido: number;
  enOrden: number;
  disponibleNeto: number;
  costoUnitario: number;
  valorTotal: number;
  ultimoMovimiento: string | null;
  diasSinMovimiento: number | null;
  estado: 'NORMAL' | 'BAJO' | 'CRITICO' | 'EXCESO';
}

export interface OrdenSugerida {
  codigo: string;
  nombre: string;
  tipo: string;
  requerido: number;
  disponible: number;
  ordenar: number;
  fechaNecesidad: string;
  accion: string;
  costoUnitario: number;
  costoTotal: number;
}

export interface ExcepcionMrp {
  codigo: string;
  nombre: string;
  tipo: string;
  tipoExcepcion: string;
  cantidadActual: number;
  cantidadMinima: number;
  cantidadAfectada: number;
  severidad: 'CRITICAL' | 'WARNING' | 'INFO';
  estado: string;
  descripcion: string;
}

export interface ActionMessage {
  codigoItem: string;
  nombreItem: string;
  tipoItem: string;
  tipoAccion: string;
  fechaAnterior: string | null;
  fechaNueva: string | null;
  cantidadAnterior: number;
  cantidadNueva: number;
  razon: string;
  prioridad: 'HIGH' | 'MEDIUM' | 'LOW';
  fechaActual: string | null;
}

/** Matches backend KpiReportDto from GerencialReportController */
export interface KpiDashboard {
  totalOrdenesSugeridas: number;
  costoTotalOrdenes: number;
  totalAcciones: number;
  accionesUrgentes: number;
  totalExcepciones: number;
  excepcionesCriticas: number;
  materialesCriticos: number;
  materialesBajos: number;
  valorTotalInventario: number;
}

/** @deprecated Use KpiDashboard */
export type DashboardSummary = KpiDashboard;

export type ExportFormat = 'pdf' | 'excel' | 'csv';
