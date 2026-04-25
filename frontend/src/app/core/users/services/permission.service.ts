import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { PermissionListItem } from '../models/permission.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';

@Injectable({
  providedIn: 'root',
})
export class PermissionService {
  private readonly http = inject(HttpClient);
  private readonly permissionsUrl = apiEndpoint(API_ROUTES.permissions.list);

  listPermissions() {
    return this.http.get<ApiResponse<PermissionListItem[]>>(this.permissionsUrl).pipe(map((response) => response.data ?? []));
  }

  createPermission(payload: { name: string; description: string }) {
    return this.http.post<ApiResponse<PermissionListItem>>(this.permissionsUrl, payload);
  }

  updatePermission(id: string, payload: { name: string; description: string }) {
    return this.http.put<ApiResponse<PermissionListItem>>(`${this.permissionsUrl}/${id}`, payload);
  }

  deletePermission(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.permissionsUrl}/${id}`);
  }
}
