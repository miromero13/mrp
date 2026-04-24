export const APP_ROUTES = {
  login: 'login',
  dashboard: 'dashboard',
  users: 'users',
  permissions: 'permissions',
  roles: 'roles',
  enterprises: 'enterprises',
  employees: 'employees',
  workshifts: 'workshifts'
} as const;

export const APP_ROUTE_URLS = {
  login: `/${APP_ROUTES.login}`,
  dashboard: `/${APP_ROUTES.dashboard}`,
  users: `/${APP_ROUTES.users}`,
  permissions: `/${APP_ROUTES.permissions}`,
  roles: `/${APP_ROUTES.roles}`,
  enterprises: `/${APP_ROUTES.enterprises}`,
  employees: `/${APP_ROUTES.employees}`,
  workshifts: `/${APP_ROUTES.workshifts}`,
} as const;
