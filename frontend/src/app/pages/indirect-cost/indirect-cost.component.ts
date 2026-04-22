import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideRefreshCcw, lucideTrash2, lucidePencil, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { finalize, take } from 'rxjs/operators';
import { AuthService } from '../../core/users/services/auth.service';
import { CustomTableColumn, CustomTableComponent } from '../../shared/components/custom-table/custom-table.component';
import { IndirectCostFormComponent } from './indirect-cost-form.component';
import { IndirectCost, IndirectCostRequest } from './models/indirect-cost.model';
import { IndirectCostService } from './services/indirect-cost.service';

@Component({
  selector: 'app-indirect-cost',
  standalone: true,
  imports: [
    CommonModule,
    CustomTableComponent,
    ...HlmButtonImports,
    ...HlmIconImports,
    IndirectCostFormComponent,
  ],
  providers: [provideIcons({ lucidePlus, lucideRefreshCcw, lucideTrash2, lucidePencil, lucideX })],
  templateUrl: './indirect-cost.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndirectCostComponent implements OnInit {
  private readonly indirectCostService = inject(IndirectCostService);
  private readonly authService = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formError = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly selectedCost = signal<IndirectCost | null>(null);
  protected readonly costs = signal<IndirectCost[]>([]);

  protected readonly tableColumns = computed<ReadonlyArray<CustomTableColumn<IndirectCost>>>(() => [
    {
      id: 'category',
      header: 'Categoría',
      cell: (cost) => cost.categoryName,
    },
    {
      id: 'amount',
      header: 'Monto',
      cell: (cost) => `${cost.amount} ${cost.currency}`,
    },
    {
      id: 'costCenter',
      header: 'Centro de Costo',
      cell: (cost) => cost.costCenterName,
    },
    {
      id: 'period',
      header: 'Periodo',
      cell: (cost) => `${cost.startDate} - ${cost.endDate}`,
    },
    {
      id: 'criterion',
      header: 'Criterio',
      cell: (cost) => cost.distributionCriterion,
    },
    {
      id: 'status',
      header: 'Estado',
      cell: (cost) => (cost.active ? 'Activo' : 'Inactivo'),
    },
    {
      id: 'actions',
      header: '',
      cell: () => '',
      align: 'right',
    },
  ]);

  ngOnInit(): void {
    this.loadCosts();
  }

  protected loadCosts(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.indirectCostService
      .findAll()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (response) => {
          if (response.data) {
            this.costs.set(response.data);
          }
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 403) {
            this.authService.logout();
            return;
          }
          const backendError = error?.error?.error ?? error?.error?.message;
          this.errorMessage.set(backendError ?? 'No se pudieron cargar los costos.');
        },
      });
  }

  protected openCreateDialog(): void {
    this.selectedCost.set(null);
    this.formError.set(null);
    this.isModalOpen.set(true);
  }

  protected closeDialog(): void {
    if (!this.saving()) {
      this.isModalOpen.set(false);
    }
  }

  protected editCost(cost: IndirectCost): void {
    this.selectedCost.set(cost);
    this.formError.set(null);
    this.isModalOpen.set(true);
  }

  protected saveCost(payload: IndirectCostRequest): void {
    this.saving.set(true);
    this.formError.set(null);

    const request = this.selectedCost()
      ? this.indirectCostService.update(this.selectedCost()!.id, payload)
      : this.indirectCostService.create(payload);

    request
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isModalOpen.set(false);
          this.loadCosts();
        },
        error: (err) => {
          this.formError.set(err.error?.message || 'Error al guardar el costo');
        },
      });
  }

  protected deleteCost(id: string): void {
    if (confirm('¿Estás seguro de desactivar este costo?')) {
      this.indirectCostService.deactivate(id).subscribe({
        next: () => this.loadCosts(),
        error: (err) => alert('Error al desactivar: ' + (err.error?.message || err.message)),
      });
    }
  }
}
