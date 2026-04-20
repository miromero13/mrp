import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { RoleFormValue, RoleListItem } from '../models/role.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  private readonly http = inject(HttpClient);
  private readonly rolesUrl = apiEndpoint(API_ROUTES.roles.list);

  listRoles() {
    return this.http.get<ApiResponse<RoleListItem[]>>(this.rolesUrl).pipe(map((response) => response.data ?? []));
  }

  createRole(payload: RoleFormValue) {
    return this.http.post<ApiResponse<RoleListItem>>(this.rolesUrl, payload);
  }
}