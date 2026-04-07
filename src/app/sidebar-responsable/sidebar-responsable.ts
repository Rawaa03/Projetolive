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
      id: 'ressources',
      label: 'Gérer ressources',
      icon: 'resources',
      route: '/ressources',
      roles: ['ADMIN', 'RESPONSABLE'],
      children: [
        {
          id: 'bennes',
          label: 'Gérer bennes',
          icon: 'bennes',
          route: '/ressources/bennes',
          roles: ['ADMIN', 'RESPONSABLE']
        },
        {
          id: 'tracteurs',
          label: 'Gérer tracteurs',
          icon: 'tracteurs',
          route: '/ressources/tracteurs',
          roles: ['ADMIN', 'RESPONSABLE']
        }
      ]
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
        console.log('User role loaded:', this.userRole);
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

    console.log('Filtering menu for role:', this.userRole);
    this.filteredMenuItems = this.menuItems
      .filter(item => {
        if (!item.roles) return true;
        return item.roles.includes(this.userRole);
      })
      .map(item => {
        if (item.children && item.children.length > 0) {
          return {
            ...item,
            children: item.children.filter(child => {
              if (!child.roles) return true;
              return child.roles.includes(this.userRole);
            })
          };
        }
        return item;
      });
    console.log('Filtered menu items:', this.filteredMenuItems);
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

  toggleMenu(menuId: string): void {
    if (this.expandedMenus.has(menuId)) {
      this.expandedMenus.delete(menuId);
    } else {
      this.expandedMenus.add(menuId);
    }
  }

  isMenuExpanded(menuId: string): boolean {
    return this.expandedMenus.has(menuId);
  }

  navigate(route: string): void {
    this.router.navigate([route]);
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
      resources: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15h-2v-6h2v6zm4-10h-2V7h2v10zm4 5h-2v-8h2v8z',
      bennes: 'M17 12h-5v5h5v-5zM16 1v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2h-1V1h-2zm3 18H5V8h14v11z',
      tracteurs: 'M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.22.42-1.42 1.01L3 12v8c0 .5.5 1 1 1h1c.5 0 1-.5 1-1v-1h12v1c0 .5.5 1 1 1h1c.5 0 1-.5 1-1v-8l-2.08-5.99zM6.5 16c-1.38 0-2.5-1.12-2.5-2.5S5.12 11 6.5 11s2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5zm11 0c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z'
    };
    return icons[iconName] || icons['dashboard'];
  }
}
