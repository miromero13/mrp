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
    dashboard: 'reports/dashboard',
    inventario: 'reports/inventario',
    inventarioExcel: 'reports/inventario/excel',
    ordenesSugeridas: 'reports/ordenes-sugeridas',
    ordenesSugeridasExcel: 'reports/ordenes-sugeridas/excel',
    ordenesSugeridasHtml: 'reports/ordenes-sugeridas/html',
    excepciones: 'reports/excepciones',
    excepcionesExcel: 'reports/excepciones/excel',
    acciones: 'reports/acciones',
    accionesExcel: 'reports/acciones/excel',
  },
} as const;
