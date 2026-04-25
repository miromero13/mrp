import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkCenter } from '../../../models/work-center.model';
import { WorkCenterService } from '../../../services/work-center.service';

@Component({
  selector: 'app-work-center-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './work-center-detail.component.html',
  styleUrls: ['./work-center-detail.component.scss']
})
export class WorkCenterDetailComponent implements OnInit {

  workCenter: WorkCenter | null = null;
  loading = false;

  constructor(
    private workCenterService: WorkCenterService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.loadWorkCenter(id);
      }
    });
  }

  loadWorkCenter(id: string): void {
    this.loading = true;
    this.workCenterService.getWorkCenterById(id).subscribe({
      next: (data) => {
        this.workCenter = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error:', error);
        this.loading = false;
      }
    });
  }

  edit(): void {
    if (this.workCenter?.id) {
      this.router.navigate(['/work-centers', this.workCenter.id, 'edit']);
    }
  }

  back(): void {
    this.router.navigate(['/work-centers']);
  }

  markAsInactive(): void {
    if (this.workCenter?.id) {
      if (confirm('¿Deseas marcar este centro como inactivo?')) {
        this.workCenterService.markAsInactive(this.workCenter.id).subscribe({
          next: () => {
            alert('Centro marcado como inactivo');
            this.back();
          },
          error: (error) => {
            console.error('Error:', error);
          }
        });
      }
    }
  }
}
