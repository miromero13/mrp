import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guards/auth.guard';
import { publicGuard } from './core/auth/guards/public.guard';
import { permissionsGuard } from './core/auth/guards/permissions.guard';
import { APP_ROUTES } from './core/config/app-routes.utils';
import { PrivateLayoutComponent } from './layouts/private-layout/private-layout.component';
import { WorkCenterListComponent } from './pages/work-centers/work-center-list/work-center-list.component';
import { WorkCenterFormComponent } from './pages/work-centers/work-center-form/work-center-form.component';
import { WorkCenterDetailComponent } from './pages/work-centers/work-center-detail/work-center-detail.component';

export const routes: Routes = [
	{
		path: APP_ROUTES.login,
		canActivate: [publicGuard],
		loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent),
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
				path: 'work-centers',
				children: [
					{
						path: '',
						component: WorkCenterListComponent,
						data: { permisos: [] },
					},
					{
						path: 'new',
						component: WorkCenterFormComponent,
						data: { permisos: [] },
					},
					{
						path: ':id',
						component: WorkCenterDetailComponent,
						data: { permisos: [] },
					},
					{
						path: ':id/edit',
						component: WorkCenterFormComponent,
						data: { permisos: [] },
					},
				],
			},
			{ path: '', pathMatch: 'full', redirectTo: APP_ROUTES.dashboard },
		],
	},
	{ path: '', pathMatch: 'full', redirectTo: APP_ROUTES.login },
	{ path: '**', redirectTo: APP_ROUTES.login },
];
