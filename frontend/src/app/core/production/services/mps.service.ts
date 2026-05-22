import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { ApiResponse } from '../../users/models/auth.models';
import { GenerateMpsFormValue, MpsRecordListItem, MpsPeriodMode, UpdateMpsRecordFormValue } from '../models/mps.models';

@Injectable({
  providedIn: 'root',
})
export class MpsService {
  private readonly http = inject(HttpClient);
  private readonly mpsUrl = apiEndpoint(API_ROUTES.production.mps);

  listMps(productId: string, periodMode: MpsPeriodMode) {
    return this.http
      .get<ApiResponse<MpsRecordListItem[]>>(this.mpsUrl, { params: { productId, periodMode } })
      .pipe(map((response) => response.data ?? []));
  }

  generateMps(payload: GenerateMpsFormValue) {
    return this.http.post<ApiResponse<MpsRecordListItem[]>>(`${this.mpsUrl}/generate`, payload).pipe(map((response) => response.data ?? []));
  }

  updateMpsRecord(id: string, payload: UpdateMpsRecordFormValue) {
    return this.http.put<ApiResponse<MpsRecordListItem>>(`${this.mpsUrl}/${id}`, payload);
  }
}
