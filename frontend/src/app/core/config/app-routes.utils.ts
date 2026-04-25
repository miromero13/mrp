export const APP_ROUTES = {
  login: 'login',
  dashboard: 'dashboard',
  users: 'users',
  permissions: 'permissions',
  roles: 'roles',
  enterprises: 'enterprises',
  machines: 'machines',
  indirectCosts: 'indirect-costs',
  workshifts: 'workshifts',
  materials: 'materials',
  products: 'products',
} as const;

export const APP_ROUTE_URLS = {
  login: `/${APP_ROUTES.login}`,
  dashboard: `/${APP_ROUTES.dashboard}`,
  users: `/${APP_ROUTES.users}`,
  permissions: `/${APP_ROUTES.permissions}`,
  roles: `/${APP_ROUTES.roles}`,
  enterprises: `/${APP_ROUTES.enterprises}`,
  machines: `/${APP_ROUTES.machines}`,
  indirectCosts: `/${APP_ROUTES.indirectCosts}`,
  workshifts: `/${APP_ROUTES.workshifts}`,
  materials: `/${APP_ROUTES.materials}`,
  products: `/${APP_ROUTES.products}`,
} as const;
