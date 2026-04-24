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
  enterprises: {
    list: 'enterprises',
  },
  employees: {
    list: 'employees',
  },
  workshifts: {
    list: 'workshifts',
  },
} as const;
