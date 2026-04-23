import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ChartsModule } from 'ng2-charts';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardLayoutComponent } from './layout/dashboard-layout.component';
import { AgriculteurDashboardComponent } from './agriculteur-dashboard/agriculteur-dashboard.component';
import { ResponsableDashboardComponent } from './responsable-dashboard/responsable-dashboard.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';

// Shared Components
import { DashboardCardComponent } from './shared/components/dashboard-card/dashboard-card.component';
import { StatChartComponent } from './shared/components/stat-chart/stat-chart.component';
import { ProgressBarComponent } from './shared/components/progress-bar/progress-bar.component';
import { DataTableComponent } from './shared/components/data-table/data-table.component';
import { AlertBadgeComponent } from './shared/components/alert-badge/alert-badge.component';
import { VergerCardComponent } from './shared/components/verger-card/verger-card.component';
import { TourneeCardComponent } from './shared/components/tournee-card/tournee-card.component';

// Services
import { DashboardService } from './shared/services/dashboard.service';

@NgModule({
  declarations: [
    DashboardLayoutComponent,
    AgriculteurDashboardComponent,
    ResponsableDashboardComponent,
    AdminDashboardComponent,
    DashboardCardComponent,
    StatChartComponent,
    ProgressBarComponent,
    DataTableComponent,
    AlertBadgeComponent,
    VergerCardComponent,
    TourneeCardComponent,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    ChartsModule,
    DashboardRoutingModule,
  ],
  providers: [DashboardService],
})
export class DashboardModule { }
