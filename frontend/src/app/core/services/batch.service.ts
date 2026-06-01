import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Batch, CreateBatchRequest } from '../models/batch.model';
import { ENV_CONFIG } from '../config/env-config';

@Injectable({
  providedIn: 'root'
})
export class BatchService {
  // Assuming environment.apiUrl exists, otherwise using a fallback.
  private apiUrl = (ENV_CONFIG as any).apiUrl ? `${(ENV_CONFIG as any).apiUrl}/api/batches` : 'http://localhost:8090/api/batches';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Batch[]> {
    return this.http.get<Batch[]>(this.apiUrl);
  }

  getById(id: string): Observable<Batch> {
    return this.http.get<Batch>(`${this.apiUrl}/${id}`);
  }

  create(request: CreateBatchRequest): Observable<Batch> {
    return this.http.post<Batch>(this.apiUrl, request);
  }

  update(id: string, request: CreateBatchRequest): Observable<Batch> {
    return this.http.put<Batch>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
