import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { ApiResponse } from '../../users/models/auth.models';
import { CreateProductionRouteFormValue, ProductionRouteListItem } from '../models/production-route.models';

@Injectable({
  providedIn: 'root',
})
export class ProductionRouteService {
  private readonly http = inject(HttpClient);
  private readonly routesUrl = apiEndpoint(API_ROUTES.enterprise.productionRoutes);

  listProductionRoutes(productVersionId?: string) {
    const params = productVersionId ? { productVersionId } : undefined;
    return this.http.get<ApiResponse<ProductionRouteListItem[]>>(this.routesUrl, { params }).pipe(map((response) => response.data ?? []));
  }

  createProductionRoute(payload: CreateProductionRouteFormValue) {
    return this.http.post<ApiResponse<ProductionRouteListItem>>(this.routesUrl, payload);
  }

  updateProductionRoute(id: string, payload: CreateProductionRouteFormValue) {
    return this.http.put<ApiResponse<ProductionRouteListItem>>(`${this.routesUrl}/${id}`, payload);
  }

  deleteProductionRoute(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.routesUrl}/${id}`);
  }
}
