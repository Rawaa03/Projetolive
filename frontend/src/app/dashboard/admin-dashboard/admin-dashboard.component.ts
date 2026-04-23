import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../shared/services/dashboard.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  dashboard: any = null;
  loading: boolean = true;
  error: string = '';

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.dashboardService.getAdminDashboard().subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard:', error);
        this.error = 'Failed to load dashboard data';
        this.loading = false;
      },
    });
  }

  getUsersByRoleEntries(): any[] {
    if (!this.dashboard || !this.dashboard.usersByRole) return [];
    return Object.entries(this.dashboard.usersByRole).map(([role, count]: any) => ({
      role,
      count,
    }));
  }

  getVergersByStatusEntries(): any[] {
    if (!this.dashboard || !this.dashboard.vergersByStatus) return [];
    return Object.entries(this.dashboard.vergersByStatus).map(([status, count]: any) => ({
      status,
      count,
    }));
  }
}
