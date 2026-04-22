export const APP_ROUTES = {
  login: 'login',
  dashboard: 'dashboard',
  users: 'users',
  permissions: 'permissions',
  roles: 'roles',
  indirectCosts: 'indirect-costs',
} as const;

export const APP_ROUTE_URLS = {
  login: `/${APP_ROUTES.login}`,
  dashboard: `/${APP_ROUTES.dashboard}`,
  users: `/${APP_ROUTES.users}`,
  permissions: `/${APP_ROUTES.permissions}`,
  roles: `/${APP_ROUTES.roles}`,
  indirectCosts: `/${APP_ROUTES.indirectCosts}`,
} as const;
