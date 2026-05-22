import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { provideIcons } from '@ng-icons/core';
import { lucideChevronDown, lucideChevronRight, lucideCog, lucideLayoutDashboard, lucideLogOut, lucidePackage, lucideReceiptText, lucideUserLock, lucideUserRoundCog, lucideUsers, lucideUser, lucideBuilding, lucideBuilding2, lucideWarehouse, lucideHandCoins, lucideBoxes, lucideHardHat, lucideClipboardClock, lucideGitBranch } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmIconImports } from '@spartan-ng/helm/icon';
import { HlmSidebarImports } from '@spartan-ng/helm/sidebar';
import { HlmSidebarService } from '@spartan-ng/helm/sidebar';
import { AuthService } from '../../core/users/services/auth.service';
import { APP_NAME } from '../../core/config/app.config';
import { APP_ROUTE_URLS } from '../../core/config/app-routes.utils';
import { PERMISOS } from '../../core/config/permisos';

interface NavigationItem {
  title: string;
  url: string;
  icon: string;
  exact?: boolean;
  requiredPermissions?: string[];
  requiredRoles?: string[];
}

interface NavigationModule {
  kind: 'module';
  title: string;
  icon: string;
  items: NavigationItem[];
  expanded: boolean;
  requiredPermissions?: string[];
  requiredRoles?: string[];
}

interface NavigationStandaloneItem {
  kind: 'item';
  title: string;
  url: string;
  icon: string;
  exact?: boolean;
  requiredPermissions?: string[];
  requiredRoles?: string[];
}

type NavigationNode = NavigationModule | NavigationStandaloneItem;

@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    ...HlmButtonImports,
    ...HlmIconImports,
    ...HlmSidebarImports,
  ],
  providers: [provideIcons({ lucideChevronDown, lucideChevronRight, lucideCog, lucideLayoutDashboard, lucideLogOut, lucidePackage, lucideReceiptText, lucideUserLock, lucideUserRoundCog, lucideUsers, lucideUser, lucideBuilding, lucideBoxes, lucideHardHat, lucideBuilding2, lucideWarehouse, lucideHandCoins, lucideClipboardClock, lucideGitBranch })],
  templateUrl: './private-layout.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PrivateLayoutComponent {
  protected readonly authService = inject(AuthService);
  protected readonly sidebarService = inject(HlmSidebarService);
  protected readonly appRouteUrls = APP_ROUTE_URLS;
  protected readonly appName = APP_NAME;
  protected readonly currentUserRoleName = computed(() => this.authService.currentUser()?.role?.name ?? '');

  protected readonly navigationNodes = signal<NavigationNode[]>([
    {
      kind: 'item',
      title: 'Dashboard',
      url: this.appRouteUrls.dashboard,
      icon: 'lucideLayoutDashboard',
      exact: true,
      requiredPermissions: [],
    },
    {
      kind: 'module',
      title: 'Usuarios',
      icon: 'lucideUser',
      requiredPermissions: [],
      items: [
        {
          title: 'Gestionar permisos',
          url: this.appRouteUrls.permissions,
          icon: 'lucideUserLock',
          exact: true,
          requiredPermissions: [PERMISOS.permisos.listar],
        },
        {
          title: 'Gestionar roles',
          url: this.appRouteUrls.roles,
          icon: 'lucideUserRoundCog',
          exact: true,
          requiredPermissions: [PERMISOS.roles.listar],
        },
        {
          title: 'Gestionar usuarios',
          url: this.appRouteUrls.users,
          icon: 'lucideUsers',
          exact: true,
          requiredPermissions: [PERMISOS.usuarios.listar],
        },
        {
          title: 'Gestionar turnos',
          url: this.appRouteUrls.workshifts,
          icon: 'lucideClipboardClock',
          exact: true,
          requiredPermissions: [PERMISOS.workshifts.listar],
        },
      ],
      expanded: true,
    },
    {
      kind: 'module',
      title: 'Empresas',
      icon: 'lucideBuilding2',
      requiredPermissions: [],
      items: [
        {
          title: 'Gestionar empresas',
          url: this.appRouteUrls.enterprises,
          icon: 'lucideBuilding',
          exact: true,
          requiredPermissions: [PERMISOS.enterprises.listar],
        },
      ],
      expanded: true,
    },
    {
      kind: 'module',
      title: 'Ingenieria',
      icon: 'lucideHardHat',
      requiredPermissions: [],
      items: [
        {
          title: 'Gestionar productos',
          url: this.appRouteUrls.products,
          icon: 'lucidePackage',
          exact: true,
          requiredPermissions: [PERMISOS.products.listar],
        },
        {
          title: ' Gestionar maquinaria',
          url: this.appRouteUrls.machines,
          icon: 'lucideCog',
          exact: true,
          requiredPermissions: [PERMISOS.machines.listar],
        },
        {
          title: 'Gestionar rutas',
          url: this.appRouteUrls.productionRoutes,
          icon: 'lucideGitBranch',
          exact: true,
          requiredPermissions: [PERMISOS.productionRoutes.listar],
        },
      ],
      expanded: true,
    },
    {
      kind: 'module',
      title: 'Produccion',
      icon: 'lucideClipboardClock',
      requiredPermissions: [],
      items: [
        {
          title: 'Gestionar demandas',
          url: this.appRouteUrls.productionDemands,
          icon: 'lucideClipboardClock',
          exact: true,
          requiredPermissions: [PERMISOS.production.demands.listar],
        },
        {
          title: 'Gestionar MPS',
          url: this.appRouteUrls.productionMps,
          icon: 'lucideClipboardClock',
          exact: true,
          requiredPermissions: [PERMISOS.production.mps.gestionar],
        },
      ],
      expanded: true,
    },
    {
      kind: 'module',
      title: 'Almacenes',
      icon: 'lucideWarehouse',
      requiredPermissions: [],
      items: [

        {
          title: 'Gestionar materiales',
          url: this.appRouteUrls.materials,
          icon: 'lucideBoxes',
          exact: true,
          requiredPermissions: [PERMISOS.material.listar],
        },
        {
          title: 'Gestionar lotes',
          url: this.appRouteUrls.batches,
          icon: 'lucidePackage',
          exact: true,
          requiredPermissions: [PERMISOS.warehouses.batches.listar],
          requiredRoles: ['Administrador'],
        },
        {
          title: 'Gestionar BOM',
          url: this.appRouteUrls.bom,
          icon: 'lucideReceiptText',
          exact: true,
          requiredPermissions: [PERMISOS.bom.listar],
        },
      ],
      expanded: true,
    },
    {
      kind: 'module',
      title: 'Finanzas',
      icon: 'lucideHandCoins',
      requiredPermissions: [],
      items: [
        {
          title: 'Gestionar costos indirectos',
          url: this.appRouteUrls.indirectCosts,
          icon: 'lucideReceiptText',
          exact: true,
          requiredPermissions: [PERMISOS.indirectCosts.listar],
        },
      ],
      expanded: true,
    }
  ]);

  protected readonly userPermissions = computed(() =>
    this.authService.currentUser()?.role?.permissions?.map((permission) => permission.name) ?? [],
  );

  protected readonly visibleNavigationNodes = computed<NavigationNode[]>(() => {
    const userPermissions = this.userPermissions();
    const userRoleName = this.currentUserRoleName();

    return this.navigationNodes().reduce<NavigationNode[]>((visibleNodes, node) => {
      if (node.kind === 'item') {
        if (this.hasRequiredPermissions(node.requiredPermissions, userPermissions) && this.hasRequiredRoles(node.requiredRoles, userRoleName)) {
          visibleNodes.push(node);
        }
        return visibleNodes;
      }

      if (!this.hasRequiredPermissions(node.requiredPermissions, userPermissions) || !this.hasRequiredRoles(node.requiredRoles, userRoleName)) {
        return visibleNodes;
      }

      const visibleItems = node.items.filter((item) => this.hasRequiredPermissions(item.requiredPermissions, userPermissions) && this.hasRequiredRoles(item.requiredRoles, userRoleName));

      if (visibleItems.length > 0) {
        visibleNodes.push({ ...node, items: visibleItems });
      }

      return visibleNodes;
    }, []);
  });

  toggleModule(title: string): void {
    this.navigationNodes.update((nodes) =>
      nodes.map((node) =>
        node.kind === 'module' && node.title === title
          ? { ...node, expanded: !node.expanded }
          : node,
      ),
    );
  }

  logout(): void {
    this.authService.logout();
  }

  private hasRequiredPermissions(requiredPermissions: string[] | undefined, userPermissions: string[]): boolean {
    if (!requiredPermissions || requiredPermissions.length === 0) {
      return true;
    }

    if (userPermissions.length === 0) {
      return false;
    }

    return requiredPermissions.every((permission) => userPermissions.includes(permission));
  }

  private hasRequiredRoles(requiredRoles: string[] | undefined, userRoleName: string): boolean {
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    return requiredRoles.some((role) => role === userRoleName);
  }
}
