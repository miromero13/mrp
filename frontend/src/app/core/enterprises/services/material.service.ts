import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../../users/models/auth.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import {
  CreateMaterialFormValue,
  CreateMaterialMovementFormValue,
  MaterialListItem,
  MaterialMovementListItem,
} from '../models/material.models';

@Injectable({
  providedIn: 'root',
})
export class MaterialService {
  private readonly http = inject(HttpClient);
  private readonly materialsUrl = apiEndpoint(API_ROUTES.enterprise.materials);
  private readonly materialMovementsUrl = apiEndpoint(API_ROUTES.enterprise.materialMovements);

  listMaterials() {
    return this.http.get<ApiResponse<MaterialListItem[]>>(this.materialsUrl).pipe(map((response) => response.data ?? []));
  }

  createMaterial(payload: CreateMaterialFormValue) {
    return this.http.post<ApiResponse<MaterialListItem>>(this.materialsUrl, payload);
  }

  updateMaterial(id: string, payload: CreateMaterialFormValue) {
    return this.http.put<ApiResponse<MaterialListItem>>(`${this.materialsUrl}/${id}`, payload);
  }

  deleteMaterial(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.materialsUrl}/${id}`);
  }

  listMaterialMovements() {
    return this.http.get<ApiResponse<MaterialMovementListItem[]>>(this.materialMovementsUrl).pipe(map((response) => response.data ?? []));
  }

  createMaterialMovement(materialId: string, payload: CreateMaterialMovementFormValue) {
    return this.http.post<ApiResponse<MaterialMovementListItem>>(`${this.materialsUrl}/${materialId}/movements`, payload);
  }
}
