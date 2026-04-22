import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, input, output, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { IndirectCostService } from './services/indirect-cost.service';
import { IndirectCost, IndirectCostRequest, LookupItem } from './models/indirect-cost.model';
import { finalize, take } from 'rxjs/operators';

@Component({
  selector: 'app-indirect-cost-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ...HlmButtonImports,
    ...HlmInputImports,
    ...HlmLabelImports,
  ],
  templateUrl: './indirect-cost-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndirectCostFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly indirectCostService = inject(IndirectCostService);

  readonly initialData = input<IndirectCost | null>(null);
  readonly loading = input(false);
  readonly serverError = input<string | null>(null);

  readonly cancelled = output<void>();
  readonly submitted = output<IndirectCostRequest>();

  protected readonly categories = signal<LookupItem[]>([]);
  protected readonly costCenters = signal<LookupItem[]>([]);
  protected readonly isLoadingLookups = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    categoryId: ['', Validators.required],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['BOB', Validators.required],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    distributionCriterion: ['PORCENTAJE_FIJO', Validators.required],
    costCenterId: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadLookups();
    if (this.initialData()) {
      const data = this.initialData()!;
      this.form.patchValue({
        categoryId: data.categoryId,
        amount: data.amount,
        currency: data.currency,
        startDate: data.startDate,
        endDate: data.endDate,
        distributionCriterion: data.distributionCriterion,
        costCenterId: data.costCenterId,
      });
    }
  }

  private loadLookups(): void {
    this.isLoadingLookups.set(true);
    // Parallel loading could be better, but let's keep it simple
    this.indirectCostService
      .getCategories()
      .pipe(take(1))
      .subscribe((res) => {
        if (res.data) this.categories.set(res.data);
      });
    this.indirectCostService
      .getCostCenters()
      .pipe(
        take(1),
        finalize(() => this.isLoadingLookups.set(false)),
      )
      .subscribe((res) => {
        if (res.data) this.costCenters.set(res.data);
      });
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitted.emit(this.form.getRawValue() as IndirectCostRequest);
  }

  protected cancel(): void {
    this.cancelled.emit();
  }
}
