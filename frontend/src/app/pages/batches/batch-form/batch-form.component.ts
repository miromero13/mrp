import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BatchService } from '../../../core/services/batch.service';
import { EnterpriseService } from '../../../core/services/enterprise.service';
import { AuthService } from '../../../core/users/services/auth.service';
import { Enterprise } from '../../../core/models/enterprise.model';
import { finalize, take } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-batch-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './batch-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BatchFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly batchService = inject(BatchService);
  private readonly enterpriseService = inject(EnterpriseService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly enterprises = signal<Enterprise[]>([]);

  batchForm: FormGroup = this.fb.group({
    batchType: ['MATERIAL', Validators.required],
    productionDate: ['', Validators.required],
    expirationDate: ['', Validators.required],
    currentStock: [0, [Validators.required, Validators.min(0)]],
    enterpriseId: ['', Validators.required],
    materialId: [''],
    productId: [''],
    status: ['APPROVED', Validators.required]
  });

  ngOnInit(): void {
    this.loadEnterprises();
  }

  protected loadEnterprises(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.enterpriseService.getAll().pipe(
      take(1),
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (data) => this.enterprises.set(data),
      error: (error: HttpErrorResponse) => {
        if (error.status === 403) {
          this.errorMessage.set('Tu sesión no es válida o expiró. Inicia sesión nuevamente.');
          this.authService.logout();
          return;
        }
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'No se pudieron cargar las empresas.';
        this.errorMessage.set(backendError ?? fallbackError);
      }
    });
  }

  protected onSubmit(): void {
    if (this.batchForm.invalid || this.loading()) return;

    this.loading.set(true);
    this.errorMessage.set(null);

    const formValue = { ...this.batchForm.value };
    if (formValue.batchType === 'MATERIAL') {
      formValue.productId = null;
    } else {
      formValue.materialId = null;
    }
    delete formValue.batchType;

    this.batchService.create(formValue).pipe(
      take(1),
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: () => {
        this.router.navigate(['/batches']);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 403) {
          this.errorMessage.set('Tu sesión no es válida o expiró. Inicia sesión nuevamente.');
          this.authService.logout();
          return;
        }
        const backendError = error?.error?.error ?? error?.error?.message;
        const fallbackError = error?.message ?? 'Error al crear el lote.';
        this.errorMessage.set(backendError ?? fallbackError);
      }
    });
  }

  protected cancel(): void {
    this.router.navigate(['/batches']);
  }
}
