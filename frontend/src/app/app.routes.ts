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
				loadComponent: () => import('./pages/users/user/user.component').then((m) => m.UserComponent),
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
				path: APP_ROUTES.enterprises,
				loadComponent: () => import('./pages/enterprise/enterprises/enterprises.component').then((m) => m.EnterprisesComponent),
				data: { permisos: [PERMISOS.enterprises.listar] },
			},
			{
				path: APP_ROUTES.machines,
				loadComponent: () => import('./pages/engineering/machines/machines.component').then((m) => m.MachinesComponent),
				data: { permisos: [PERMISOS.machines.listar] },
			},
			{
				path: APP_ROUTES.indirectCosts,
				loadComponent: () => import('./pages/finance/indirect-costs/indirect-costs.component').then((m) => m.IndirectCostsComponent),
				data: { permisos: [PERMISOS.indirectCosts.listar] },
			},
			{
				path: APP_ROUTES.workshifts,
				loadComponent: () => import('./pages/users/workshifts/workshifts.component').then((m) => m.WorkShiftsComponent),
				data: { permisos: [PERMISOS.workshifts.listar] },
			},
			{
				path: APP_ROUTES.materials,
				loadComponent: () => import('./pages/warehouses/materials/materials.component').then((m) => m.MaterialsComponent),
				data: { permisos: [PERMISOS.material.listar] },
			},
			{
				path: APP_ROUTES.batches,
				loadComponent: () => import('./pages/warehouses/batches/batches.component').then((m) => m.BatchesComponent),
				data: { permisos: [PERMISOS.warehouses.batches.listar], roles: ['Administrador'] },
			},
			{
				path: APP_ROUTES.products,
				loadComponent: () => import('./pages/engineering/products/products.component').then((m) => m.ProductsComponent),
				data: { permisos: [PERMISOS.products.listar] },
			},
			{
				path: APP_ROUTES.bom,
				loadComponent: () => import('./pages/warehouses/bom/bom.component').then((m) => m.BomComponent),
				data: { permisos: [PERMISOS.bom.listar] },
			},
			{
				path: APP_ROUTES.productionDemands,
				loadComponent: () => import('./pages/production/demands/demands.component').then((m) => m.DemandsComponent),
				data: { permisos: [PERMISOS.production.demands.listar] },
			},
			{
				path: APP_ROUTES.productionMps,
				loadComponent: () => import('./pages/production/mps/mps.component').then((m) => m.MpsComponent),
				data: { permisos: [PERMISOS.production.mps.gestionar] },
			},
			{
				path: APP_ROUTES.productionRoutes,
				loadComponent: () => import('./pages/engineering/production-routes/production-routes.component').then((m) => m.ProductionRoutesComponent),
				data: { permisos: [PERMISOS.productionRoutes.listar] },
			},
			{ path: '', pathMatch: 'full', redirectTo: APP_ROUTES.dashboard },
		],
	},
	{ path: '', pathMatch: 'full', redirectTo: APP_ROUTES.login },
	{ path: '**', redirectTo: APP_ROUTES.login },
];
