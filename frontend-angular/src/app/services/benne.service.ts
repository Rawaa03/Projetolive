import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Benne, BenneCreation } from '../models/benne.model';

@Injectable({
  providedIn: 'root'
})
export class BenneService {
  private apiUrl = 'http://localhost:8080/api/ressources/bennes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Benne[]> {
    return this.http.get<Benne[]>(this.apiUrl);
  }

  getById(id: string): Observable<Benne> {
    return this.http.get<Benne>(`${this.apiUrl}/${id}`);
  }

  getByType(type: string): Observable<Benne[]> {
    return this.http.get<Benne[]>(`${this.apiUrl}/type/${type}`);
  }

  getByStatut(statut: string): Observable<Benne[]> {
    return this.http.get<Benne[]>(`${this.apiUrl}/statut/${statut}`);
  }

  getAvailable(startDate: string, endDate: string): Observable<Benne[]> {
    return this.http.get<Benne[]>(`${this.apiUrl}/available`, {
      params: { startDate, endDate }
    });
  }

  create(benne: BenneCreation): Observable<Benne> {
    return this.http.post<Benne>(this.apiUrl, benne);
  }

  update(id: string, benne: Partial<Benne>): Observable<Benne> {
    return this.http.put<Benne>(`${this.apiUrl}/${id}`, benne);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  chargerBenne(id: string, quantite: number): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/charger`, { quantite });
  }

  viderBenne(id: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/vider`, {});
  }

  startMaintenance(id: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/maintenance/start`, {});
  }

  endMaintenance(id: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/maintenance/end`, {});
  }

  assignToTournee(id: string, tourneeId: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/assign-tournee`, { tourneeId });
  }

  unassignFromTournee(id: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/unassign-tournee`, {});
  }

  assignTractor(benneId: string, tracteurId: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${benneId}/assign-tractor`, { tracteurId });
  }
}
