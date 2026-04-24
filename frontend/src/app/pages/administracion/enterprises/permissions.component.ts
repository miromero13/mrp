import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { finalize, take } from 'rxjs/operators';
import { PermissionListItem } from '../../../core/users/models/permission.models';
import { AuthService } from '../../../core/users/services/auth.service';
import { PermissionService } from '../../../core/users/services/permission.service';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';

@Component({
  selector: 'app-permissions',
  standalone: true,
  imports: [CustomTableComponent, ...HlmButtonImports],
  templateUrl: './permissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermissionsComponent implements OnInit {
  private readonly permissionService = inject(PermissionService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly permissions = signal<PermissionListItem[]>([]);

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<PermissionListItem>>>(() => [
    {
      id: 'name',
      header: 'Permiso',
      cell: (permission) => permission.name,
    },
    {
      id: 'description',
      header: 'Descripcion',
      cell: (permission) => permission.description || '-',
    },
  ]);

  ngOnInit(): void {
    this.loadPermissions();
  }

  protected loadPermissions(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.permissionService
      .listPermissions()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (permissions) => this.permissions.set(permissions),
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.errorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los permisos.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
