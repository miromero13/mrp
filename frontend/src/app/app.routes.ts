import { Routes } from '@angular/router';
import { authGuard } from './core/users/guards/auth.guard';
import { publicGuard } from './core/users/guards/public.guard';
import { permissionsGuard } from './core/users/guards/permissions.guard';
import { APP_ROUTES } from './core/config/app-routes.utils';
import { PERMISOS } from './core/config/permisos';
import { PrivateLayoutComponent } from './layouts/private-layout/private-layout.component';

export const routes: Routes = [
  {
    path: APP_ROUTES.login,
    canActivate: [publicGuard],
    loadComponent: () => import('./pages/users/auth/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    canActivate: [authGuard],
    component: PrivateLayoutComponent,
    canActivateChild: [permissionsGuard],
    children: [
      {
        path: APP_ROUTES.dashboard,
        loadComponent: () => import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
        data: { permisos: [] },
      },
      {
        path: APP_ROUTES.users,
        loadComponent: () => import('./pages/users/list/users.component').then((m) => m.UsersComponent),
        data: { permisos: [PERMISOS.usuarios.listar] },
      },
      {
        path: APP_ROUTES.permissions,
        loadComponent: () => import('./pages/users/permissions/permissions.component').then((m) => m.PermissionsComponent),
        data: { permisos: [PERMISOS.permisos.listar] },
      },
      {
        path: APP_ROUTES.roles,
        loadComponent: () => import('./pages/users/roles/roles.component').then((m) => m.RolesComponent),
        data: { permisos: [PERMISOS.roles.listar] },
      },
      {
        path: APP_ROUTES.indirectCosts,
        loadComponent: () =>
          import('./pages/indirect-cost/indirect-cost.component').then((m) => m.IndirectCostComponent),
        data: { permisos: [PERMISOS.indirectCosts.listar] },
      },
      { path: '', pathMatch: 'full', redirectTo: APP_ROUTES.dashboard },
    ],
  },
  { path: '', pathMatch: 'full', redirectTo: APP_ROUTES.login },
  { path: '**', redirectTo: APP_ROUTES.login },
];
