import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs/operators';
import { provideIcons } from '@ng-icons/core';
import { lucidePencil, lucidePlus, lucideTrash2, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { CustomTableColumn, CustomTableComponent } from '../../../shared/components/custom-table/custom-table.component';
import { IndirectCostFormValue, IndirectCostListItem } from '../../../core/enterprises/models/indirect-cost.models';
import { IndirectCostService } from '../../../core/enterprises/services/indirect-cost.service';

@Component({
  selector: 'app-indirect-costs',
  standalone: true,
  imports: [CustomTableComponent, ReactiveFormsModule, ...HlmButtonImports, ...HlmIconImports, ...HlmInputImports, ...HlmLabelImports],
  providers: [provideIcons({ lucidePlus, lucidePencil, lucideTrash2, lucideX })],
  templateUrl: './indirect-costs.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndirectCostsComponent implements OnInit {
  @ViewChild('indirectCostActions', { static: true })
  private readonly indirectCostActionsTemplate!: TemplateRef<{ $implicit: IndirectCostListItem }>;

  private readonly formBuilder = inject(FormBuilder);
  private readonly indirectCostService = inject(IndirectCostService);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly formErrorMessage = signal<string | null>(null);
  protected readonly isModalOpen = signal(false);
  protected readonly editingIndirectCostId = signal<string | null>(null);
  protected readonly indirectCosts = signal<IndirectCostListItem[]>([]);
  protected tableColumns: ReadonlyArray<CustomTableColumn<IndirectCostListItem>> = [];

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(255)]],
    amount: [0, [Validators.required, Validators.min(0)]],
  });

  ngOnInit(): void {
    this.tableColumns = [
      { id: 'name', header: 'Nombre', cell: (cost) => cost.name },
      { id: 'description', header: 'Descripción', cell: (cost) => cost.description ?? '-' },
      { id: 'amount', header: 'Monto', cell: (cost) => String(cost.amount) },
      {
        id: 'actions',
        header: 'Acciones',
        cell: () => '',
        template: this.indirectCostActionsTemplate,
        align: 'right',
      },
    ];
    this.loadIndirectCosts();
  }

  protected openModal(indirectCost: IndirectCostListItem | null = null): void {
    this.formErrorMessage.set(null);
    this.editingIndirectCostId.set(indirectCost?.id ?? null);
    this.form.reset({
      name: indirectCost?.name ?? '',
      description: indirectCost?.description ?? '',
      amount: indirectCost?.amount ?? 0,
    });
    this.isModalOpen.set(true);
  }

  protected closeModal(): void {
    if (this.saving()) {
      return;
    }

    this.formErrorMessage.set(null);
    this.isModalOpen.set(false);
    this.editingIndirectCostId.set(null);
  }

  protected saveIndirectCost(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: IndirectCostFormValue = {
      name: value.name.trim(),
      description: value.description.trim() || null,
      amount: Number(value.amount),
    };

    this.saving.set(true);
    this.formErrorMessage.set(null);

    const request$ = this.editingIndirectCostId()
      ? this.indirectCostService.updateIndirectCost(this.editingIndirectCostId() as string, payload)
      : this.indirectCostService.createIndirectCost(payload);

    request$
      .pipe(
        take(1),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.isModalOpen.set(false);
          this.editingIndirectCostId.set(null);
          this.loadIndirectCosts();
        },
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudo guardar el costo indirecto.';
          this.formErrorMessage.set(backendError ?? fallbackError);
        },
      });
  }

  protected deleteIndirectCost(id: string): void {
    if (!confirm('¿Eliminar este costo indirecto?')) {
      return;
    }

    this.indirectCostService.deleteIndirectCost(id).pipe(take(1)).subscribe({
      next: () => this.loadIndirectCosts(),
      error: (error: HttpErrorResponse) => {
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudo eliminar el costo indirecto.';
        this.errorMessage.set(backendError ?? fallbackError);
      },
    });
  }

  protected loadIndirectCosts(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.indirectCostService
      .listIndirectCosts()
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (indirectCosts) => this.indirectCosts.set(indirectCosts),
        error: (error: HttpErrorResponse) => {
          const backendError = error?.error?.error ?? error?.error?.message;
          const fallbackError = error?.message ?? 'No se pudieron cargar los costos indirectos.';
          this.errorMessage.set(backendError ?? fallbackError);
        },
      });
  }
}
