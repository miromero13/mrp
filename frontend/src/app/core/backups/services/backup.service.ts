import { HttpClient, HttpParams } from '@angular/common/http';
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

  listBackups(payload:BackupFormValue) {
    const params = new HttpParams()
        .set('userId', payload.userId)
        .set('enterpriseId', payload.enterpriseId);
    return this.http.get<ApiResponse<BackupItem[]>>(this.backupsUrl, { params }).pipe(map((response) => response.data ?? []));
  }

  createManualBackup(payload: BackupFormValue) {
    return this.http.post<ApiResponse<void>>(`${this.backupsUrl}/manual`, payload);
  }

  createScheduledBackup(schedulePayload: ScheduleBackupFormValue, createPayload: BackupFormValue) {
    return this.http.post<ApiResponse<void>>(`${this.backupsUrl}/programar`, { ...createPayload, ...schedulePayload });
  }

}