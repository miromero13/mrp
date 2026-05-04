// components/movement-form/movement-form.component.ts
import { Component, Input, Output, EventEmitter, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MaterialService } from '../../core/users/services/material.service';
import { Material } from '../../core/users/models/material.model';

@Component({
  selector: 'app-movement-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movement-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MovementFormComponent implements OnInit {
  @Input() material!: Material;
  @Input() movementType: 'ENTRADA' | 'SALIDA' = 'ENTRADA';
  @Output() saved     = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private readonly fb              = inject(FormBuilder);
  private readonly materialService = inject(MaterialService);

  loading  = signal(false);
  errorMsg = signal('');

  form = this.fb.group({
    amount: [null, [Validators.required, Validators.min(0.01)]]
  });

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.errorMsg.set('');

    const dto = {
      movementType: this.movementType,
      amount: this.form.value.amount as unknown as number
    };

    const action$ = this.movementType === 'ENTRADA'
      ? this.materialService.logRevenue(this.material.id, dto)
      : this.materialService.logExpense(this.material.id, dto);

    action$.subscribe({
      next: res => {
        if (res.statusCode === 200) {
          this.saved.emit();
        } else {
          this.errorMsg.set(res.error ?? 'Error al registrar movimiento');
        }
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error inesperado al registrar movimiento');
        this.loading.set(false);
      }
    });
  }
}