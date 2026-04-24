import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { EnterpriseListItem } from '../models/enterprise.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';

@Injectable({
  providedIn: 'root',
})
export class EnterpriseService {
  private readonly http = inject(HttpClient);
  private readonly enterprisesUrl = apiEndpoint(API_ROUTES.enterprises.list);

  listEnterprises() {
    return this.http.get<ApiResponse<EnterpriseListItem[]>>(this.enterprisesUrl).pipe(map((response) => response.data ?? []));
  }
}
