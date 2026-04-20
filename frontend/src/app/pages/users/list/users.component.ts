import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize, take } from 'rxjs/operators';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { AuthService } from '../../../core/users/services/auth.service';
import { UserListItem } from '../../../core/users/models/user.models';
import { UserService } from '../../../core/users/services/user.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CustomTableComponent],
  templateUrl: './users.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsersComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly users = signal<UserListItem[]>([]);

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<UserListItem>>>(() => [
    {
      id: 'name',
      header: 'Usuario',
      cell: (user) => user.name,
    },
    {
      id: 'email',
      header: 'Email',
      cell: (user) => user.email,
    },
    {
      id: 'phone',
      header: 'Telefono',
      cell: (user) => user.phone || '-',
    },
    {
      id: 'role',
      header: 'Rol',
      cell: (user) => user.role?.name || '-',
    },
  ]);

  ngOnInit(): void {
    this.loadUsers();
  }

  protected loadUsers(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.userService
      .listUsers()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (users) => this.users.set(users),
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.errorMessage.set('Tu sesion no es valida. Inicia sesion nuevamente.');
            this.authService.logout();
            return;
          }

          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los usuarios.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}