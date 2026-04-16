import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <main class="grid gap-4">
      <section class="rounded-xl border bg-card p-4 text-card-foreground shadow-xs">
        <h1 class="text-base font-semibold">Dashboard</h1>
        <p class="pt-1 text-sm text-muted-foreground">
          El layout privado ya esta activo. Si ves este bloque, el sidebar y el enrutado funcionan.
        </p>
      </section>
    </main>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent {}
