import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../../users/models/auth.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { IndirectCostFormValue, IndirectCostListItem } from '../models/indirect-cost.models';

@Injectable({
  providedIn: 'root',
})
export class IndirectCostService {
  private readonly http = inject(HttpClient);
  private readonly indirectCostsUrl = apiEndpoint(API_ROUTES.enterprise.indirectCosts);

  listIndirectCosts() {
    return this.http.get<ApiResponse<IndirectCostListItem[]>>(this.indirectCostsUrl).pipe(map((response) => response.data ?? []));
  }

  createIndirectCost(payload: IndirectCostFormValue) {
    return this.http.post<ApiResponse<IndirectCostListItem>>(this.indirectCostsUrl, payload);
  }

  updateIndirectCost(id: string, payload: IndirectCostFormValue) {
    return this.http.put<ApiResponse<IndirectCostListItem>>(`${this.indirectCostsUrl}/${id}`, payload);
  }

  deleteIndirectCost(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.indirectCostsUrl}/${id}`);
  }
}
