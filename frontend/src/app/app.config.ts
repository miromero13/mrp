import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { authInterceptor } from './core/users/interceptors/auth.interceptor';
import { AuthService } from './core/users/services/auth.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideAppInitializer(() => {
      const authService = inject(AuthService);
      return authService.initializeSession();
    }),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideRouter(routes),
  ]
};
