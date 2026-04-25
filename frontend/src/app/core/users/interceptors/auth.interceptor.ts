import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  const authLoginUrl = apiEndpoint(API_ROUTES.auth.login);

  if (!token) {
    return next(req).pipe(
      catchError((error) => {
        if ((error.status === 401 || error.status === 403) && req.url !== authLoginUrl) {
          authService.logout();
        }

        return throwError(() => error);
      }),
    );
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(authReq).pipe(
    catchError((error) => {
      if ((error.status === 401 || error.status === 403) && req.url !== authLoginUrl) {
        authService.logout();
      }

      return throwError(() => error);
    }),
  );
};
