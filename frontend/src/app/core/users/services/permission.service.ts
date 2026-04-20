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
}
