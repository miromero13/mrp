import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { APP_ROUTE_URLS } from '../../config/app-routes.utils';

export const publicGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated() ? router.createUrlTree([APP_ROUTE_URLS.dashboard]) : true;
};
