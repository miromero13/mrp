import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { ApiResponse } from '../../users/models/auth.models';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { MachineFormValue, MachineListItem } from '../models/machine.models';

@Injectable({
  providedIn: 'root',
})
export class MachineService {
  private readonly http = inject(HttpClient);
  private readonly machinesUrl = apiEndpoint(API_ROUTES.enterprise.machines);

  listMachines() {
    return this.http.get<ApiResponse<MachineListItem[]>>(this.machinesUrl).pipe(map((response) => response.data ?? []));
  }

  createMachine(payload: MachineFormValue) {
    return this.http.post<ApiResponse<MachineListItem>>(this.machinesUrl, payload);
  }

  updateMachine(id: string, payload: MachineFormValue) {
    return this.http.put<ApiResponse<MachineListItem>>(`${this.machinesUrl}/${id}`, payload);
  }

  deleteMachine(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.machinesUrl}/${id}`);
  }
}
