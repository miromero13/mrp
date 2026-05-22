import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { ApiResponse } from '../../users/models/auth.models';
import { DemandFormValue, DemandListItem } from '../models/demand.models';

@Injectable({
  providedIn: 'root',
})
export class DemandService {
  private readonly http = inject(HttpClient);
  private readonly demandsUrl = apiEndpoint(API_ROUTES.production.demands);

  listDemands() {
    return this.http.get<ApiResponse<DemandListItem[]>>(this.demandsUrl).pipe(map((response) => response.data ?? []));
  }

  createDemand(payload: DemandFormValue) {
    return this.http.post<ApiResponse<DemandListItem>>(this.demandsUrl, payload);
  }

  updateDemand(id: string, payload: Partial<DemandFormValue>) {
    return this.http.put<ApiResponse<DemandListItem>>(`${this.demandsUrl}/${id}`, payload);
  }

  cancelDemand(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.demandsUrl}/${id}`);
  }
}
