import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardLayoutComponent } from './layout/dashboard-layout.component';
import { AgriculteurDashboardComponent } from './agriculteur-dashboard/agriculteur-dashboard.component';
import { ResponsableDashboardComponent } from './responsable-dashboard/responsable-dashboard.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardLayoutComponent,
    children: [
      {
        path: 'agriculteur',
        component: AgriculteurDashboardComponent,
      },
      {
        path: 'responsable',
        component: ResponsableDashboardComponent,
      },
      {
        path: 'admin',
        component: AdminDashboardComponent,
      },
      {
        path: '',
        redirectTo: 'agriculteur',
        pathMatch: 'full',
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule { }
