export const APP_ROUTES = {
  login: 'login',
  dashboard: 'dashboard',
  users: 'users',
  permissions: 'permissions',
  roles: 'roles',
} as const;

export const APP_ROUTE_URLS = {
  login: `/${APP_ROUTES.login}`,
  dashboard: `/${APP_ROUTES.dashboard}`,
  users: `/${APP_ROUTES.users}`,
  permissions: `/${APP_ROUTES.permissions}`,
  roles: `/${APP_ROUTES.roles}`,
} as const;
