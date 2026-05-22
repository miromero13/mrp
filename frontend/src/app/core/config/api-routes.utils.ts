export const API_ROUTES = {
  auth: {
    login: 'auth/login',
    session: 'auth/session',
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
  enterprises: {
    list: 'enterprises',
  },
  enterprise: {
    materials: 'enterprise/materials',
    materialMovements: 'enterprise/materials/movements',
    batches: 'enterprise/batches',
    products: 'enterprise/products',
    bomItems: 'enterprise/bom-items',
    productionRoutes: 'enterprise/production-routes',
    machines: 'enterprise/machines',
    indirectCosts: 'enterprise/indirect-costs',
  },
  production: {
    demands: 'enterprise/demands',
    mps: 'enterprise/mps',
  },
  workshifts: {
    list: 'work-shifts',
  },
} as const;
