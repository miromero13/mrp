import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkCenter, CreateWorkCenterRequest, UpdateWorkCenterRequest } from '../../../models/work-center.model';
import { WorkCenterService } from '../../../services/work-center.service';

@Component({
  selector: 'app-work-center-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './work-center-form.component.html',
  styleUrls: ['./work-center-form.component.scss']
})
export class WorkCenterFormComponent implements OnInit {

  form!: FormGroup;
  isEditMode = false;
  workCenterId: string | null = null;
  loading = false;
  submitting = false;

  resourceTypes = ['MACHINE', 'LABOR', 'EQUIPMENT', 'LINE'];
  statuses = ['ACTIVE', 'INACTIVE', 'MAINTENANCE', 'SCHEDULED'];

  constructor(
    private fb: FormBuilder,
    private workCenterService: WorkCenterService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.initializeForm();
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id && id !== 'new') {
        this.isEditMode = true;
        this.workCenterId = id;
        this.loadWorkCenter(id);
      }
    });
  }

  initializeForm(): void {
    this.form = this.fb.group({
      code: ['', [Validators.required]],
      name: ['', [Validators.required]],
      description: [''],
      plant: ['', [Validators.required]],
      productionLine: [''],
      resourceType: ['', [Validators.required]],
      capacity: ['', [Validators.required, Validators.min(0.1)]],
      costPerHour: ['', [Validators.required, Validators.min(0)]],
      targetEfficiency: ['', [Validators.required, Validators.min(0), Validators.max(100)]],
      currentOee: [''],
      isBottleneck: [false],
      isCriticalResource: [false],
      status: ['ACTIVE'],
      calendar: [''],
      nextMaintenanceDate: ['']
    });
  }

  loadWorkCenter(id: string): void {
    this.loading = true;
    this.workCenterService.getWorkCenterById(id).subscribe({
      next: (workCenter) => {
        this.form.patchValue(workCenter);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar centro de trabajo:', error);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      return;
    }

    this.submitting = true;

    if (this.isEditMode && this.workCenterId) {
      const request: UpdateWorkCenterRequest = this.form.value;
      this.workCenterService.updateWorkCenter(this.workCenterId, request).subscribe({
        next: () => {
          alert('Centro de trabajo actualizado exitosamente');
          this.router.navigate(['/work-centers']);
        },
        error: (error) => {
          console.error('Error:', error);
          alert('Error al actualizar el centro de trabajo');
          this.submitting = false;
        }
      });
    } else {
      const request: CreateWorkCenterRequest = this.form.value;
      this.workCenterService.createWorkCenter(request).subscribe({
        next: () => {
          alert('Centro de trabajo creado exitosamente');
          this.router.navigate(['/work-centers']);
        },
        error: (error) => {
          console.error('Error:', error);
          alert('Error al crear el centro de trabajo');
          this.submitting = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/work-centers']);
  }
}
