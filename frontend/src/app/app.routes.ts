import { Routes } from '@angular/router';
import { authGuard } from './core/users/guards/auth.guard';
import { publicGuard } from './core/users/guards/public.guard';
import { permissionsGuard } from './core/users/guards/permissions.guard';
import { APP_ROUTES } from './core/config/app-routes.utils';
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
			{ path: '', pathMatch: 'full', redirectTo: APP_ROUTES.dashboard },
		],
	},
	{ path: '', pathMatch: 'full', redirectTo: APP_ROUTES.login },
	{ path: '**', redirectTo: APP_ROUTES.login },
];
