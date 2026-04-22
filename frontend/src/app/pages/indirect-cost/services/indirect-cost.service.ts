import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../core/users/models/auth.models';
import { apiEndpoint } from '../../../core/config/api.config';
import { API_ROUTES } from '../../../core/config/api-routes.utils';
import { IndirectCost, IndirectCostRequest, LookupItem } from '../models/indirect-cost.model';

@Injectable({
  providedIn: 'root',
})
export class IndirectCostService {
  private readonly http = inject(HttpClient);

  findAll(): Observable<ApiResponse<IndirectCost[]>> {
    return this.http.get<ApiResponse<IndirectCost[]>>(apiEndpoint(API_ROUTES.indirectCosts.base));
  }

  create(payload: IndirectCostRequest): Observable<ApiResponse<IndirectCost>> {
    return this.http.post<ApiResponse<IndirectCost>>(apiEndpoint(API_ROUTES.indirectCosts.base), payload);
  }

  update(id: string, payload: IndirectCostRequest): Observable<ApiResponse<IndirectCost>> {
    return this.http.put<ApiResponse<IndirectCost>>(apiEndpoint(`${API_ROUTES.indirectCosts.base}/${id}`), payload);
  }

  deactivate(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(apiEndpoint(`${API_ROUTES.indirectCosts.base}/${id}`));
  }

  getCategories(): Observable<ApiResponse<LookupItem[]>> {
    return this.http.get<ApiResponse<LookupItem[]>>(apiEndpoint(API_ROUTES.indirectCosts.categories));
  }

  getCostCenters(): Observable<ApiResponse<LookupItem[]>> {
    return this.http.get<ApiResponse<LookupItem[]>>(apiEndpoint(API_ROUTES.indirectCosts.costCenters));
  }
}
