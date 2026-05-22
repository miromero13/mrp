import { inject } from '@angular/core';
import { CanActivateChildFn, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { APP_ROUTE_URLS } from '../../config/app-routes.utils';

function hasRequiredPermissions(requiredPermissions: string[] | undefined, userPermissions: string[] | undefined): boolean {
  if (!requiredPermissions || requiredPermissions.length === 0) {
    return true;
  }

  if (!userPermissions || userPermissions.length === 0) {
    return false;
  }

  return requiredPermissions.every((permission) => userPermissions.includes(permission));
}

function hasRequiredRoles(requiredRoles: string[] | undefined, userRoleName: string | null | undefined): boolean {
  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  if (!userRoleName) {
    return false;
  }

  return requiredRoles.includes(userRoleName);
}

export const permissionsGuard: CanActivateChildFn = (childRoute, _state: RouterStateSnapshot): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const requiredPermissions = childRoute.data['permisos'] as string[] | undefined;
  const requiredRoles = childRoute.data['roles'] as string[] | undefined;
  const userPermissions = authService.currentUser()?.role?.permissions?.map((permission) => permission.name);
  const userRoleName = authService.currentUser()?.role?.name;

  if (hasRequiredPermissions(requiredPermissions, userPermissions) && hasRequiredRoles(requiredRoles, userRoleName)) {
    return true;
  }

  return router.createUrlTree([APP_ROUTE_URLS.dashboard]);
};
