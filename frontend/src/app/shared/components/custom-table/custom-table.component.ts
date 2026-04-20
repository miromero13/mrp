import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, input, signal } from '@angular/core';
import { HlmSkeletonImports } from '@spartan-ng/helm/skeleton';
import { HlmTableImports } from '@spartan-ng/helm/table';

export type TableColumnAlign = 'left' | 'center' | 'right';

export interface CustomTableColumn<T> {
  id: string;
  header: string;
  cell: (row: T) => string;
  align?: TableColumnAlign;
}

@Component({
  selector: 'app-custom-table',
  standalone: true,
  imports: [CommonModule, ...HlmTableImports, ...HlmSkeletonImports],
  templateUrl: './custom-table.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomTableComponent<T> implements OnInit {
  readonly columns = input.required<ReadonlyArray<CustomTableColumn<T>>>();
  readonly rows = input<ReadonlyArray<T>>([]);
  readonly loading = input(false);
  readonly pageSize = input(20);
  readonly skeletonRows = input(8);
  readonly maxHeight = input('30rem');
  readonly emptyMessage = input('No hay datos para mostrar.');

  private readonly visibleCount = signal(20);

  readonly visibleRows = computed(() => this.rows().slice(0, this.visibleCount()));
  readonly hasMoreRows = computed(() => this.visibleRows().length < this.rows().length);
  readonly skeletonIndexes = computed(() => Array.from({ length: this.skeletonRows() }, (_, index) => index));

  ngOnInit(): void {
    this.visibleCount.set(this.pageSize());
  }

  onScroll(event: Event): void {
    if (this.loading() || !this.hasMoreRows()) {
      return;
    }

    const element = event.target as HTMLElement;
    const distanceToBottom = element.scrollHeight - element.scrollTop - element.clientHeight;
    const threshold = 80;

    if (distanceToBottom <= threshold) {
      this.visibleCount.update((current) => Math.min(current + this.pageSize(), this.rows().length));
    }
  }

  trackColumn = (_index: number, column: CustomTableColumn<T>): string => column.id;

  trackRow = (index: number): number => index;
}
