import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { apiEndpoint } from '../../config/api.config';
import { API_ROUTES } from '../../config/api-routes.utils';
import { ApiResponse, BackupFormValue, BackupItem, ScheduleBackupFormValue } from '../models/backup.models';

@Injectable({
  providedIn: 'root',
})
export class BackupService {
  private readonly http = inject(HttpClient);
  private readonly backupsUrl = apiEndpoint(API_ROUTES.backups.list);

  listBackups() {
    // acceder a .../api/backups
    return this.http.get<ApiResponse<BackupItem[]>>(this.backupsUrl).pipe(map((response) => response.data ?? []));
  }

  createManualBackup(payload: BackupFormValue) {
    return this.http.post<ApiResponse<void>>(`${this.backupsUrl}/manual`, payload);
  }

  createScheduledBackup(schedulePayload: ScheduleBackupFormValue, createPayload: BackupFormValue) {
    return this.http.post<ApiResponse<void>>(this.backupsUrl, { ...createPayload, ...schedulePayload });
  }

  deleteBackup(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.backupsUrl}/${id}`);
  }

  // descargar un backup, recibir un archivo
  downloadBackup(id: string) {
    return this.http.get(`${this.backupsUrl}/${id}/download`, {
      responseType: 'blob',
    });
  }
  
  

  /* deleteEnterprise(id: string) {
    return this.http.delete<ApiResponse<void>>(`${this.enterprisesUrl}/${id}`);
  } */

  /* getBackup(id: string,) {
    return this.http.get<ApiResponse<BackupItem>>(`${this.backupsUrl}/${id}`).pipe(map((response) => response.data ?? null));
  } */
}