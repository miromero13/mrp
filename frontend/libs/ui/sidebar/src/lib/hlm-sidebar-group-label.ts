import { Directive } from '@angular/core';
import { classes } from '@spartan-ng/helm/utils';

@Directive({
  selector: 'div[hlmSidebarGroupLabel], button[hlmSidebarGroupLabel]',
  host: {
    'data-slot': 'sidebar-group-label',
    'data-sidebar': 'group-label',
  },
})
export class HlmSidebarGroupLabel {
  constructor() {
    classes(() => [
      'text-sidebar-foreground/70 ring-sidebar-ring flex h-8 shrink-0 items-center rounded-md px-2 text-xs font-medium transition-[margin,colors,background-color,box-shadow,opacity] duration-200 ease-linear outline-none hover:bg-sidebar-accent hover:text-sidebar-accent-foreground focus-visible:bg-sidebar-accent focus-visible:text-sidebar-accent-foreground focus-visible:ring-2 active:bg-sidebar-accent active:text-sidebar-accent-foreground [&>_ng-icon]:size-4 [&>_ng-icon]:shrink-0 hover:cursor-pointer focus-visible:cursor-pointer',
      'group-data-[collapsible=icon]:-mt-8 group-data-[collapsible=icon]:opacity-0',
    ]);
  }
}
