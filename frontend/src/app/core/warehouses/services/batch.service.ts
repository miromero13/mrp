import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { ApiResponse } from '../../users/models/auth.models';
import { BatchListItem, CreateBatchFormValue, UpdateBatchFormValue } from '../models/batch.models';

export interface BatchQueryFilters {
  enterpriseId?: string;
  materialId?: string;
  productId?: string;
}

@Injectable({
  providedIn: 'root',
})
export class BatchService {
  private readonly http = inject(HttpClient);
  private readonly batchesUrl = apiEndpoint(API_ROUTES.enterprise.batches);

  listBatches(filters: BatchQueryFilters = {}) {
    let params = new HttpParams();

    if (filters.enterpriseId) {
      params = params.set('enterpriseId', filters.enterpriseId);
    }
    if (filters.materialId) {
      params = params.set('materialId', filters.materialId);
    }
    if (filters.productId) {
      params = params.set('productId', filters.productId);
    }

    return this.http.get<ApiResponse<BatchListItem[]>>(this.batchesUrl, { params }).pipe(map((response) => response.data ?? []));
  }

  createBatch(payload: CreateBatchFormValue) {
    return this.http.post<ApiResponse<BatchListItem>>(this.batchesUrl, payload);
  }

  updateBatch(id: string, payload: UpdateBatchFormValue) {
    return this.http.put<ApiResponse<BatchListItem>>(`${this.batchesUrl}/${id}`, payload);
  }

  deleteBatch(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.batchesUrl}/${id}`);
  }
}
