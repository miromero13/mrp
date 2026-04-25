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
    products: 'enterprise/products',
    machines: 'enterprise/machines',
    indirectCosts: 'enterprise/indirect-costs',
  },
  workshifts: {
    list: 'work-shifts',
  },
} as const;
