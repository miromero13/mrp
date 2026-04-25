import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { WorkCenter } from '../../../models/work-center.model';
import { WorkCenterService } from '../../../services/work-center.service';

@Component({
  selector: 'app-work-center-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './work-center-list.component.html',
  styleUrls: ['./work-center-list.component.scss']
})
export class WorkCenterListComponent implements OnInit {

  workCenters: WorkCenter[] = [];
  filteredWorkCenters: WorkCenter[] = [];
  loading = false;
  searchText = '';
  filterStatus = 'ALL';

  constructor(
    private workCenterService: WorkCenterService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadWorkCenters();
  }

  loadWorkCenters(): void {
    this.loading = true;
    this.workCenterService.getAllWorkCenters().subscribe({
      next: (data) => {
        this.workCenters = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar centros de trabajo:', error);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredWorkCenters = this.workCenters.filter(wc => {
      const matchesSearch = wc.name.toLowerCase().includes(this.searchText.toLowerCase()) ||
        wc.code.toLowerCase().includes(this.searchText.toLowerCase());
      const matchesStatus = this.filterStatus === 'ALL' || wc.status === this.filterStatus;
      return matchesSearch && matchesStatus;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onStatusFilterChange(): void {
    this.applyFilters();
  }

  viewDetail(id: string): void {
    this.router.navigate(['/work-centers', id]);
  }

  editWorkCenter(id: string): void {
    this.router.navigate(['/work-centers', id, 'edit']);
  }

  deleteWorkCenter(id: string): void {
    if (confirm('¿Estás seguro de que deseas eliminar este centro de trabajo?')) {
      this.workCenterService.deleteWorkCenter(id).subscribe({
        next: () => {
          this.loadWorkCenters();
        },
        error: (error) => {
          console.error('Error al eliminar:', error);
        }
      });
    }
  }

  markAsInactive(id: string): void {
    this.workCenterService.markAsInactive(id).subscribe({
      next: () => {
        this.loadWorkCenters();
      },
      error: (error) => {
        console.error('Error al marcar como inactivo:', error);
      }
    });
  }

  createNew(): void {
    this.router.navigate(['/work-centers/new']);
  }
}
