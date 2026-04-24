import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../models/auth.models';
import { EmployeeListItem } from '../models/employee.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';

@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly employeesUrl = apiEndpoint(API_ROUTES.employees.list);

  listEnterprises() {
    return this.http.get<ApiResponse<EmployeeListItem[]>>(this.employeesUrl).pipe(map((response) => response.data ?? []));
  }
}
