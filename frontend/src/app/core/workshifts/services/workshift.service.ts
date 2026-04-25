import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { WorkShiftListItem, CreateWorkShiftFormValue } from '../models/workshift.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';

@Injectable({
  providedIn: 'root',
})
export class WorkShiftService {
  private readonly http = inject(HttpClient);
  private readonly workshiftsUrl = apiEndpoint(API_ROUTES.workshifts.list);

  listWorkShifts() {
    return this.http.get<ApiResponse<WorkShiftListItem[]>>(this.workshiftsUrl).pipe(map((response) => response.data ?? []));
  }

  createWorkShift(payload: CreateWorkShiftFormValue) {
    return this.http.post<ApiResponse<WorkShiftListItem>>(this.workshiftsUrl, payload);
  }
}
