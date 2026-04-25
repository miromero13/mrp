import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../../users/models/auth.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { CreateProductFormValue, ProductListItem } from '../models/product.models';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly productsUrl = apiEndpoint(API_ROUTES.enterprise.products);

  listProducts() {
    return this.http.get<ApiResponse<ProductListItem[]>>(this.productsUrl).pipe(map((response) => response.data ?? []));
  }

  createProduct(payload: CreateProductFormValue) {
    return this.http.post<ApiResponse<ProductListItem>>(this.productsUrl, payload);
  }

  updateProduct(id: string, payload: CreateProductFormValue) {
    return this.http.put<ApiResponse<ProductListItem>>(`${this.productsUrl}/${id}`, payload);
  }

  deleteProduct(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.productsUrl}/${id}`);
  }
}
