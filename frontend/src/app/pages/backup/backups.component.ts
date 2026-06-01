import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../shared/components/custom-table/custom-table.component';
import { BackupService } from '../../core/backups/services/backup.service';
import { AuthService } from '../../core/users/services/auth.service';
import { BackupItem, BackupFormValue } from '../../core/backups/models/backup.models';
import { PERMISOS } from '../../core/config/permisos';
import { BackupFormComponent } from "./backup-form.component";
import { ScheduledBackupFormComponent, ScheduledBackupSubmitPayload } from "./scheduled-backup-form.component";

@Component({
  selector: 'app-backups',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports, BackupFormComponent, ScheduledBackupFormComponent],
  providers: [provideIcons({ lucidePlus, lucideX })],
  templateUrl: './backups.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BackupsComponent implements OnInit {
  private readonly backupService = inject(BackupService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly backupErrorMessage = signal<string | null>(null);
  protected readonly backup = signal<BackupItem | null>(null);
  protected readonly createErrorMessage = signal<string | null>(null);
  protected readonly isCreateManualModalOpen = signal(false);
  protected readonly isCreateScheduledModalOpen = signal(false);
  protected readonly backups = signal<BackupItem[]>([]);

  protected readonly adminPermissionNames = computed(
    () => new Set(this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? []),
  );

  protected readonly canCreateBackup = computed(() => this.adminPermissionNames().has(PERMISOS.backup.crear));
  protected readonly hasSingleBackup = computed(() => this.backups().length === 1);
  protected readonly selectedBackup = computed(() => this.backups()[0] ?? null);
  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<BackupItem>>>(() => [
    {
      id: 'fileName',
      header: 'Nombre archivo',
      cell: (backup) => backup.fileName,
    },
    {
      id: 'filePath',
      header: 'Ruta archivo',
      cell: (backup) => backup.filePath,
    },
    {
      id: 'type',
      header: 'Tipo',
      cell: (backup) => backup.type.toString(),
    },
    {
      id: 'state',
      header: 'Estado',
      cell: (backup) => backup.state.toString(),
    },
    {
      id: 'startDate',
      header: 'Fecha',
      cell: (backup) => backup.startDate,
    }
  ]);

  ngOnInit(): void {
    this.loadBackups({
      userId: this.authService.currentUser()?.id ?? '',
      enterpriseId: this.authService.currentUser()?.enterprise?.id ?? '',
    });
  }

  
  protected openCreateManualModal(): void {
    if (!this.canCreateBackup()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateManualModalOpen.set(true);
  }

  protected openCreateScheduledModal(): void {
    if (!this.canCreateBackup()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateScheduledModalOpen.set(true);
  }

  protected closeCreateModal(): void {
    if (this.saving()) {
      return;
    }

    this.createErrorMessage.set(null);
    this.isCreateManualModalOpen.set(false);
    this.isCreateScheduledModalOpen.set(false);
  }

  protected createManualBackup(payload: BackupFormValue): void {
    // imprimir en consola los datos de payload
    console.log('Payload.enterpriseId para backup manual: ', payload.enterpriseId);
    console.log('Payload.userId para backup manual: ', payload.userId);

    this.saving.set(true);
    this.createErrorMessage.set(null);
    this.backupService
      .createManualBackup(payload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateManualModalOpen.set(false);
          this.loadBackups(payload);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 401 || error.status === 403) {
            //this.errorMessage.set('Tu sesión expiró. Inicia sesión nuevamente.');
            //this.authService.logout();
            return;
          }
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo crear el backup.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected createScheduledBackup(payload: ScheduledBackupSubmitPayload): void {
    console.log('Payload.enterpriseId para backup programado: ', payload.createPayload.enterpriseId);
    console.log('Payload.userId para backup programado: ', payload.createPayload.userId);
    this.saving.set(true);
    this.createErrorMessage.set(null);

    this.backupService
      .createScheduledBackup(payload.schedulePayload, payload.createPayload)
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isCreateScheduledModalOpen.set(false);
          this.loadBackups(payload.createPayload);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 401 || error.status === 403) {
            //this.errorMessage.set('Tu sesión expiró. Inicia sesión nuevamente.');
            //this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo programar el backup.';
          this.createErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected loadBackups(payload: BackupFormValue): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.backupErrorMessage.set(null);

    this.backupService
      .listBackups(payload)
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (backups) => {
          this.backups.set(backups);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 401 || error.status === 403) {
            //this.errorMessage.set('Tu sesión expiró. Inicia sesión nuevamente.');
            //this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los backups.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
