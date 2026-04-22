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
} as const;
