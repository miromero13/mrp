// services/material.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { Material, CreateMaterialDto, UpdateMaterialDto } from '../models/material.model';
import { CreateMovementDto } from '../models/movement.model';

@Injectable({ providedIn: 'root' })
export class MaterialService {
    private readonly http = inject(HttpClient);
    private readonly apiUrl = 'http://localhost:8080/materials';

    getAll(): Observable<ApiResponse<Material[]>> {
        return this.http.get<ApiResponse<Material[]>>(this.apiUrl);
    }

    getById(id: string): Observable<ApiResponse<Material>> {
        return this.http.get<ApiResponse<Material>>(`${this.apiUrl}/${id}`);
    }

    create(dto: CreateMaterialDto): Observable<ApiResponse<Material>> {
        return this.http.post<ApiResponse<Material>>(this.apiUrl, dto);
    }

    update(id: string, dto: UpdateMaterialDto): Observable<ApiResponse<Material>> {
        return this.http.put<ApiResponse<Material>>(`${this.apiUrl}/${id}`, dto);
    }

    delete(id: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
    }

    logRevenue(id: string, dto: CreateMovementDto): Observable<ApiResponse<void>> {
        return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${id}/revenue`, dto);
    }

    logExpense(id: string, dto: CreateMovementDto): Observable<ApiResponse<void>> {
        return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${id}/expense`, dto);
    }
}