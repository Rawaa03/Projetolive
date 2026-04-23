import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) { }

  getAgriculteurDashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/agriculteur`);
  }

  getResponsableDashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/responsable`);
  }

  getAdminDashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/admin`);
  }

  getCurrentUserDashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/current`);
  }
}
