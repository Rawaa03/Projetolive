import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tracteur, TracteurCreation } from '../models/tracteur.model';

@Injectable({
  providedIn: 'root'
})
export class TracteurService {
  private apiUrl = 'http://localhost:8080/api/ressources/tracteurs';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(this.apiUrl);
  }

  getById(id: string): Observable<Tracteur> {
    return this.http.get<Tracteur>(`${this.apiUrl}/${id}`);
  }

  getByType(type: string): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(`${this.apiUrl}/type/${type}`);
  }

  getByStatut(statut: string): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(`${this.apiUrl}/statut/${statut}`);
  }

  getByFuelType(carburant: string): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(`${this.apiUrl}/carburant/${carburant}`);
  }

  getAvailable(startDate: string, endDate: string): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(`${this.apiUrl}/available`, {
      params: { startDate, endDate }
    });
  }

  create(tracteur: TracteurCreation): Observable<Tracteur> {
    return this.http.post<Tracteur>(this.apiUrl, tracteur);
  }

  update(id: string, tracteur: Partial<Tracteur>): Observable<Tracteur> {
    return this.http.put<Tracteur>(`${this.apiUrl}/${id}`, tracteur);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateKilometrage(id: string, kilometrage: number): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/kilometrage`, { kilometrage });
  }

  estimateConsumption(id: string, distance: number): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/${id}/estimate-consumption`, { distance });
  }

  startMaintenance(id: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/maintenance/start`, {});
  }

  endMaintenance(id: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/maintenance/end`, {});
  }

  assignOperator(id: string, operatorId: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/assign-operator`, { operatorId });
  }

  unassignOperator(id: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/unassign-operator`, {});
  }

  assignToTournee(id: string, tourneeId: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/assign-tournee`, { tourneeId });
  }

  unassignFromTournee(id: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/unassign-tournee`, {});
  }
}
