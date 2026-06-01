export const API_ROUTES = {
  auth: {
    login: 'auth/login',
  },
  users: {
    list: 'users',
  },
  permissions: {
    list: 'permissions',
  },
  roles: {
    list: 'roles',
  },
  indirectCosts: {
    base: 'indirect-costs',
    categories: 'indirect-cost-categories',
    costCenters: 'cost-centers',
  },
  reports: {
    // Gerencial
    dashboard: 'reports/gerenciales/dashboard',
    dashboardExport: 'reports/gerenciales/export',
    // Analítico — datos JSON
    inventario: 'reports/analiticos/inventario',
    ordenesSugeridas: 'reports/analiticos/ordenes-sugeridas',
    excepciones: 'reports/analiticos/excepciones',
    acciones: 'reports/analiticos/acciones',
    // Analítico — exportación (query param ?format=pdf|excel|csv)
    inventarioExport: 'reports/analiticos/inventario/export',
    ordenesSugeridasExport: 'reports/analiticos/ordenes-sugeridas/export',
    excepcionesExport: 'reports/analiticos/excepciones/export',
    accionesExport: 'reports/analiticos/acciones/export',
  },
} as const;
