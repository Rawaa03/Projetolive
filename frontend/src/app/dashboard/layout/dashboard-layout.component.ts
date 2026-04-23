import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard-layout',
  templateUrl: './dashboard-layout.component.html',
  styleUrls: ['./dashboard-layout.component.css'],
})
export class DashboardLayoutComponent implements OnInit {
  userRole: string = '';
  userName: string = '';
  sidebarOpen: boolean = true;

  constructor(private router: Router) { }

  ngOnInit(): void {
    // Get user info from localStorage or auth service
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      const user = JSON.parse(userInfo);
      this.userRole = user.role || '';
      this.userName = user.prenom + ' ' + user.nom || '';
    }
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  navigateTo(dashboard: string): void {
    this.router.navigate(['/dashboard', dashboard]);
  }

  logout(): void {
    localStorage.removeItem('userInfo');
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
