export enum BackupType {
  MANUAL, PROGRAMADO
}

export enum BackupState {
    PENDIENTE, EN_PROGRESO, COMPLETADO, ERROR
}

export interface BackupItem {
    id: string;
    enterpriseId: string;
    fileName: string;
    filePath: string;
    sizeBytes: number;
    type: BackupType;
    state: BackupState;
    startDate: string;
    endDate: string;
    message: string;
}

export interface BackupFormValue {
  userId: string;
  enterpriseId: string;
}

export interface ScheduleBackupFormValue {
  cronExpression: string;
}

export interface ApiResponse<T> {
  statusCode: number;
  message: string;
  error: string | null;
  data: T | null;
  countData: number | null;
}