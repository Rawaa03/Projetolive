import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

export interface MenuItem {
  id: string;
  label: string;
  icon: string;
  route: string;
  roles?: string[];
  children?: MenuItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-responsable.html',
  styleUrls: ['./sidebar-responsable.css']
})
export class SideBarResponsable implements OnInit {
  @Input() isCollapsed: boolean = false;
  @Output() toggleSidebar = new EventEmitter<void>();
  @Input() userRole: string = '';

  activeRoute: string = '';
  expandedMenus: Set<string> = new Set();
  unreadAlerts: number = 0;
  ressourcesMenuOpen: boolean = false;

  userProfile: any = {
    prenom: '',
    nom: '',
    role: ''
  };

  menuItems: MenuItem[] = [
    {
      id: 'dashboard',
      label: 'Tableau de bord',
      icon: 'dashboard',
      route: '/dashboard',
      roles: ['ADMIN', 'RESPONSABLE']
    },
    {
      id: 'ressources',
      label: 'Ressources',
      icon: 'resources',
      route: '',
      roles: ['ADMIN', 'RESPONSABLE'],
      children: [
        {
          id: 'bennes',
          label: 'Bennes',
          icon: 'bin',
          route: '/ressources/bennes',
          roles: ['ADMIN', 'RESPONSABLE']
        },
        {
          id: 'tracteurs',
          label: 'Tracteurs',
          icon: 'tractor',
          route: '/ressources/tracteurs',
          roles: ['ADMIN', 'RESPONSABLE']
        }
      ]
    },
    {
      id: 'travailleurs',
      label: 'Travailleurs',
      icon: 'people',
      route: '/travailleurs',
      roles: ['ADMIN', 'RESPONSABLE']
    },
    {
      id: 'agriculteurs',
      label: 'Agriculteurs',
      icon: 'orchard',
      route: '/agriculteurs',
      roles: ['ADMIN', 'RESPONSABLE']
    },
    {
      id: 'vergers',
      label: 'Vergers',
      icon: 'orchard',
      route: '/vergers',
      roles: ['ADMIN', 'RESPONSABLE']
    },
    {
      id: 'tournees',
      label: 'Tournées',
      icon: 'route',
      route: '/tournees',
      roles: ['ADMIN', 'RESPONSABLE', 'EQUIPE_RECOLTE']
    },
    {
      id: 'alertes',
      label: 'Alertes',
      icon: 'alert',
      route: '/alertes',
      roles: ['ADMIN', 'RESPONSABLE', 'AGRICULTEUR']
    },
    {
      id: 'activation',
      label: 'Activation des comptes',
      icon: 'verified',
      route: '/admin/activation',
      roles: ['ADMIN']
    },
    {
      id: 'utilisateurs',
      label: 'Gestion des utilisateurs',
      icon: 'admin',
      route: '/utilisateurs',
      roles: ['ADMIN']
    },
    {
      id: 'profile',
      label: 'Mon profil',
      icon: 'profile',
      route: '/profile',
      roles: ['ADMIN', 'RESPONSABLE', 'AGRICULTEUR', 'EQUIPE_RECOLTE']
    }
  ];

  filteredMenuItems: MenuItem[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadUserProfile();
    this.filterMenuByRole();
    this.setActiveRoute();
    this.loadUnreadAlerts();

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.setActiveRoute();
      this.ressourcesMenuOpen = false;
    });
  }

  loadUserProfile(): void {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        this.userProfile = {
          prenom: user.prenom || '',
          nom: user.nom || '',
          role: user.role || this.userRole
        };

        if (!this.userRole && user.role) {
          this.userRole = user.role.toUpperCase();
        }
      } catch (e) {
        console.error('Error parsing user data', e);
        this.setDefaultProfile();
      }
    } else {
      this.setDefaultProfile();
    }
  }

  setDefaultProfile(): void {
    this.userProfile = {
      prenom: 'Utilisateur',
      nom: '',
      role: this.userRole || 'VISITEUR'
    };
  }

  loadUnreadAlerts(): void {
    this.unreadAlerts = 0;
  }

  filterMenuByRole(): void {
    if (!this.userRole) {
      const userStr = localStorage.getItem('currentUser');
      if (userStr) {
        try {
          const user = JSON.parse(userStr);
          this.userRole = user.role?.toUpperCase() || '';
        } catch (e) {
          console.error('Error parsing user data', e);
        }
      }
    }

    this.filteredMenuItems = this.menuItems.filter(item => {
      if (!item.roles) return true;
      return item.roles.includes(this.userRole);
    });
  }

  setActiveRoute(): void {
    this.activeRoute = this.router.url;
  }

  isActive(route: string): boolean {
    if (route === '/') {
      return this.activeRoute === route;
    }
    return this.activeRoute.startsWith(route);
  }

  isRessourceActive(): boolean {
    return this.activeRoute.startsWith('/ressources');
  }

  toggleRessourcesMenu(): void {
    this.ressourcesMenuOpen = !this.ressourcesMenuOpen;
    this.expandedMenus.clear();
  }

  navigate(route: string): void {
    if (route) {
      this.router.navigate([route]);
      this.ressourcesMenuOpen = false;
    }
  }

  toggle(): void {
    this.toggleSidebar.emit();
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
  }

  getIconPath(iconName: string): string {
    const icons: { [key: string]: string } = {
      dashboard: 'M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z',
      people: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z',
      orchard: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5',
      route: 'M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z',
      alert: 'M12 2L1 21h22L12 2zm1 16h-2v-2h2v2zm0-4h-2v-4h2v4z',
      admin: 'M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z',
      profile: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z',
      verified: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z',
      resources: 'M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-1.5 0a6.5 6.5 0 1 0-13 0 6.5 6.5 0 0 0 13 0zM8 12a4 4 0 1 1 0-8 4 4 0 0 1 0 8zm0 1A5 5 0 1 0 8 3a5 5 0 0 0 0 10z',
      bin: 'M19 4h-2.5l-.71-.71c-.18-.18-.44-.29-.7-.29H9.91c-.26 0-.52.11-.7.29L8.5 4H6c-.55 0-1 .45-1 1s.45 1 1 1h12c.55 0 1-.45 1-1s-.45-1-1-1zM18 6H6v15c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V6zm-2 13l-4-4-4 4h8z',
      tractor: 'M22 11V3h-5v2h-2V3h-5v8h2V5h2v6h5zm-2 8v-6h-6v8h6zM4 13H2v6h2v-6zm16-8v2H6V5h14zM4 19h16v-2H4v2zM6 7v2h12V7H6z'
    };
    return icons[iconName] || icons['dashboard'];
  }
}
