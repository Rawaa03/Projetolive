import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../shared/services/dashboard.service';

@Component({
  selector: 'app-agriculteur-dashboard',
  templateUrl: './agriculteur-dashboard.component.html',
  styleUrls: ['./agriculteur-dashboard.component.css'],
})
export class AgriculteurDashboardComponent implements OnInit {
  dashboard: any = null;
  loading: boolean = true;
  error: string = '';

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.dashboardService.getAgriculteurDashboard().subscribe({
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
}
