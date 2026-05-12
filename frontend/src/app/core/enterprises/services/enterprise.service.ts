import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { CreateEnterpriseFormValue, EnterpriseFormValue, EnterpriseListItem } from '../models/enterprise.models';
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

  getEnterprise(id: string) {
    return this.http.get<ApiResponse<EnterpriseListItem>>(`${this.enterprisesUrl}/${id}`).pipe(map((response) => response.data ?? null));
  }

  createEnterprise(payload: CreateEnterpriseFormValue) {
    return this.http.post<ApiResponse<EnterpriseListItem>>(this.enterprisesUrl, payload);
  }

  updateEnterprise(id: string, payload: EnterpriseFormValue) {
    return this.http.put<ApiResponse<EnterpriseListItem>>(`${this.enterprisesUrl}/${id}`, payload);
  }
}
