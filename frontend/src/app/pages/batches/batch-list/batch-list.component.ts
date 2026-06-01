import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { BatchService } from '../../../core/services/batch.service';
import { Batch } from '../../../core/models/batch.model';
import { AuthService } from '../../../core/users/services/auth.service';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { finalize, take } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-batch-list',
  standalone: true,
  imports: [CommonModule, RouterModule, CustomTableComponent],
  templateUrl: './batch-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BatchListComponent implements OnInit {
  private readonly batchService = inject(BatchService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly batches = signal<Batch[]>([]);

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<Batch>>>(() => [
    {
      id: 'batchNumber',
      header: 'Número de Lote',
      cell: (batch) => batch.batchNumber,
    },
    {
      id: 'status',
      header: 'Estado',
      cell: (batch) => batch.status || '-',
    },
    {
      id: 'productionDate',
      header: 'Fecha Producción',
      cell: (batch) => batch.productionDate,
    },
    {
      id: 'expirationDate',
      header: 'Fecha Vencimiento',
      cell: (batch) => batch.expirationDate,
    },
    {
      id: 'currentStock',
      header: 'Stock Actual',
      cell: (batch) => batch.currentStock.toString(),
    },
  ]);

  ngOnInit(): void {
    this.loadBatches();
  }

  protected loadBatches(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.batchService.getAll().pipe(
      take(1),
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (data) => this.batches.set(data),
      error: (error: HttpErrorResponse) => {
        if (error.status === 403) {
          this.errorMessage.set('Tu sesión no es válida o expiró. Inicia sesión nuevamente.');
          this.authService.logout();
          return;
        }
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'Error al cargar los lotes.';
        this.errorMessage.set(backendError ?? fallbackError);
      }
    });
  }

  protected editBatch(batch: Batch): void {
    // Navigate to edit form or handle edit
    // this.router.navigate(['/batches', batch.id, 'edit']);
  }

  protected deleteBatch(batch: Batch): void {
    if (confirm('¿Está seguro de que desea eliminar este lote?')) {
      this.batchService.delete(batch.id).pipe(
        take(1)
      ).subscribe({
        next: () => {
          this.loadBatches();
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.errorMessage.set('Tu sesión no es válida o expiró. Inicia sesión nuevamente.');
            this.authService.logout();
            return;
          }
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'Error al eliminar el lote.';
          this.errorMessage.set(backendError ?? fallbackError);
        }
      });
    }
  }
}
