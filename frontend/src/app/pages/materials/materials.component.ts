import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MaterialService } from '../../core/users/services/material.service';
import { Material } from '../../core/users/models/material.model';
import { MaterialFormComponent } from './materials-form.component';
import { MovementFormComponent } from './movement-form.component';

@Component({
  selector: 'app-material-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MaterialFormComponent, MovementFormComponent],
  templateUrl: './materials.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MaterialListComponent implements OnInit {
  private readonly materialService = inject(MaterialService);

  materials   = signal<Material[]>([]);
  loading     = signal(false);
  errorMsg    = signal('');
  showForm    = signal(false);
  showMovement = signal(false);
  selectedMaterial = signal<Material | null>(null);
  movementType = signal<'ENTRADA' | 'SALIDA'>('ENTRADA');

  ngOnInit(): void {
    this.loadMaterials();
  }

  loadMaterials(): void {
    this.loading.set(true);
    this.errorMsg.set('');
    this.materialService.getAll().subscribe({
      next: res => {
        this.materials.set(res.data ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error al cargar los materiales');
        this.loading.set(false);
      }
    });
  }

  openCreate(): void {
    this.selectedMaterial.set(null);
    this.showForm.set(true);
  }

  openEdit(mat: Material): void {
    this.selectedMaterial.set(mat);
    this.showForm.set(true);
  }

  openMovement(mat: Material, type: 'ENTRADA' | 'SALIDA'): void {
    this.selectedMaterial.set(mat);
    this.movementType.set(type);
    this.showMovement.set(true);
  }

  confirmDelete(mat: Material): void {
    if (!confirm(`¿Eliminar "${mat.name}"?`)) return;
    this.materialService.delete(mat.id).subscribe({
      next: () => this.loadMaterials(),
      error: () => this.errorMsg.set('Error al eliminar el material')
    });
  }

  onSaved(): void {
    this.showForm.set(false);
    this.showMovement.set(false);
    this.loadMaterials();
  }
}