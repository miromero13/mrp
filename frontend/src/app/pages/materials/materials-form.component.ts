import { Component, Input, Output, EventEmitter, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MaterialService } from '../../core/users/services/material.service';
import { Material } from '../../core/users/models/material.model';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmSwitchImports } from '@spartan-ng/helm/switch';

@Component({
  selector: 'app-material-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ...HlmButtonImports, ...HlmInputImports, ...HlmLabelImports, ...HlmSwitchImports],
  templateUrl: './materials-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialFormComponent implements OnInit {
  @Input() material: Material | null = null;
  @Output() saved     = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  private readonly fb              = inject(FormBuilder);
  private readonly materialService = inject(MaterialService);

  loading  = signal(false);
  errorMsg = signal('');

  form = this.fb.group({
    code:         ['', Validators.required],
    name:         ['', Validators.required],
    description:  [''],
    measureUnit:  ['', Validators.required],
    currentStock: [0, [Validators.required, Validators.min(0)]],
    minimumStock: [0, [Validators.required, Validators.min(0)]],
    state:        [true]
  });

  ngOnInit(): void {
    if (this.material) {
      this.form.patchValue(this.material);
      this.form.get('code')?.disable();
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.errorMsg.set('');

    const action$ = this.material
      ? this.materialService.update(this.material.id, this.form.value as any)
      : this.materialService.create(this.form.value as any);

    action$.subscribe({
      next: res => {
        if (res.statusCode === 200) {
          this.saved.emit();
        } else {
          this.errorMsg.set(res.error ?? 'Error al guardar');
        }
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error inesperado al guardar');
        this.loading.set(false);
      }
    });
  }
}