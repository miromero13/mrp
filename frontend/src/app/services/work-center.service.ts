import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WorkCenter, CreateWorkCenterRequest, UpdateWorkCenterRequest } from '../models/work-center.model';
import { apiEndpoint } from '../core/config/api.config';

@Injectable({
  providedIn: 'root'
})
export class WorkCenterService {

  private apiUrl = apiEndpoint('work-centers');

  constructor(private http: HttpClient) { }

  getAllWorkCenters(): Observable<WorkCenter[]> {
    return this.http.get<WorkCenter[]>(this.apiUrl);
  }

  getWorkCenterById(id: string): Observable<WorkCenter> {
    return this.http.get<WorkCenter>(`${this.apiUrl}/${id}`);
  }

  getWorkCentersByPlant(plant: string): Observable<WorkCenter[]> {
    return this.http.get<WorkCenter[]>(`${this.apiUrl}/plant/${plant}`);
  }

  getWorkCentersByProductionLine(productionLine: string): Observable<WorkCenter[]> {
    return this.http.get<WorkCenter[]>(`${this.apiUrl}/line/${productionLine}`);
  }

  getActiveWorkCenters(): Observable<WorkCenter[]> {
    return this.http.get<WorkCenter[]>(`${this.apiUrl}/active`);
  }

  getCriticalResources(): Observable<WorkCenter[]> {
    return this.http.get<WorkCenter[]>(`${this.apiUrl}/critical-resources`);
  }

  createWorkCenter(request: CreateWorkCenterRequest): Observable<WorkCenter> {
    return this.http.post<WorkCenter>(this.apiUrl, request);
  }

  updateWorkCenter(id: string, request: UpdateWorkCenterRequest): Observable<WorkCenter> {
    return this.http.put<WorkCenter>(`${this.apiUrl}/${id}`, request);
  }

  deleteWorkCenter(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  markAsInactive(id: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/inactive`, {});
  }
}
