export const APP_ROUTES = {
  login: 'login',
  dashboard: 'dashboard',
} as const;

export const APP_ROUTE_URLS = {
  login: `/${APP_ROUTES.login}`,
  dashboard: `/${APP_ROUTES.dashboard}`,
} as const;
