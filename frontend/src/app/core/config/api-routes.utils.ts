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
  workshifts: {
    list: 'work-shifts',
  },
} as const;
