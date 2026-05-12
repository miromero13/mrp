import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom, map, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiResponse, AuthUser, LoginRequest, LoginResponseData } from '../models/auth.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { APP_ROUTE_URLS } from '../../config/app-routes.utils';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly tokenStorageKey = 'access_token';
  private readonly userStorageKey = 'auth_user';
  private readonly authUrl = apiEndpoint(API_ROUTES.auth.login);
  private readonly sessionUrl = apiEndpoint(API_ROUTES.auth.session);

  private readonly canUseStorage = typeof window !== 'undefined' && typeof localStorage !== 'undefined';

  private readonly tokenSignal = signal<string | null>(
    this.canUseStorage ? localStorage.getItem(this.tokenStorageKey) : null,
  );

  readonly isAuthenticated = computed(() => !!this.tokenSignal());
  readonly currentUser = signal<AuthUser | null>(this.readUserFromStorage());

  login(payload: LoginRequest) {
    return this.http.post<ApiResponse<LoginResponseData>>(this.authUrl, payload).pipe(
      map((response) => {
        const token = this.extractToken(response);
        if (!token) {
          throw new Error(response.error ?? response.message ?? 'No se recibio token de autenticacion.');
        }

        this.setUser(response.data?.user ?? null);

        this.setToken(token);
      }),
      catchError((error) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo iniciar sesion.';
        return throwError(() => new Error(backendError ?? fallbackError));
      }),
    );
  }

  initializeSession(): Promise<void> {
    if (!this.getToken()) {
      return Promise.resolve();
    }

    return firstValueFrom(
      this.http.get<ApiResponse<AuthUser>>(this.sessionUrl).pipe(
        map((response) => {
          if (!response.data) {
            throw new Error(response.error ?? response.message ?? 'No se pudo actualizar la sesion.');
          }

          this.setUser(response.data);
        }),
        catchError((error) => {
          if (error?.status === 401 || error?.status === 403 || error?.status === 404) {
            this.clearAuthState();
          }

          return of(void 0);
        }),
      ),
    ).then(() => void 0);
  }

  logout(): void {
    this.clearAuthState();
    void this.router.navigateByUrl(APP_ROUTE_URLS.login);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  updateCurrentEnterprise(enterprise: AuthUser['enterprise']): void {
    const currentUser = this.currentUser();
    if (!currentUser) {
      return;
    }

    this.setUser({
      ...currentUser,
      enterprise,
    });
  }

  private extractToken(response: ApiResponse<LoginResponseData>): string | null {
    const data = response.data as (LoginResponseData & { accessToken?: string; token?: string }) | null;
    return data?.access_token ?? data?.accessToken ?? data?.token ?? null;
  }

  private setToken(token: string): void {
    this.tokenSignal.set(token);
    if (this.canUseStorage) {
      localStorage.setItem(this.tokenStorageKey, token);
    }
  }

  private setUser(user: AuthUser | null): void {
    this.currentUser.set(user);
    if (!this.canUseStorage) {
      return;
    }

    if (user) {
      localStorage.setItem(this.userStorageKey, JSON.stringify(user));
    } else {
      localStorage.removeItem(this.userStorageKey);
    }
  }

  private readUserFromStorage(): AuthUser | null {
    if (!this.canUseStorage) {
      return null;
    }

    const raw = localStorage.getItem(this.userStorageKey);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      localStorage.removeItem(this.userStorageKey);
      return null;
    }
  }

  private clearToken(): void {
    this.tokenSignal.set(null);
    this.setUser(null);
    if (this.canUseStorage) {
      localStorage.removeItem(this.tokenStorageKey);
    }
  }

  private clearAuthState(): void {
    this.clearToken();
  }
}
