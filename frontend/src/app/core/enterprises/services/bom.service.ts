import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../../users/models/auth.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { BomItemListItem, CreateBomItemFormValue } from '../models/bom.models';

@Injectable({
  providedIn: 'root',
})
export class BomService {
  private readonly http = inject(HttpClient);
  private readonly bomItemsUrl = apiEndpoint(API_ROUTES.enterprise.bomItems);

  listBomItems(productVersionId: string) {
    return this.http
      .get<ApiResponse<BomItemListItem[]>>(this.bomItemsUrl, { params: { productVersionId } })
      .pipe(map((response) => response.data ?? []));
  }

  createBomItem(payload: CreateBomItemFormValue) {
    return this.http.post<ApiResponse<BomItemListItem>>(this.bomItemsUrl, payload);
  }

  updateBomItem(id: string, payload: Partial<CreateBomItemFormValue>) {
    return this.http.put<ApiResponse<BomItemListItem>>(`${this.bomItemsUrl}/${id}`, payload);
  }

  deleteBomItem(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.bomItemsUrl}/${id}`);
  }
}
