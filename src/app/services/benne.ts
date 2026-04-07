import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Benne, BenneCreation } from '../models/benne';

@Injectable({
  providedIn: 'root'
})
export class BenneService {
  private apiUrl = 'http://localhost:8080/api/ressources';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Benne[]> {
    return this.http.get<Benne[]>(`${this.apiUrl}?type=BENNE`).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: string): Observable<Benne> {
    return this.http.get<Benne>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  create(benne: BenneCreation): Observable<Benne> {
    // Convert frontend format to backend format
    const ressourceData = {
      type: 'BENNE',
      nom: `Benne ${new Date().toLocaleDateString()}`,
      capaciteKg: benne.capaciteMax,
      tauxRemplissage: 0,
      estPleine: false,
      quantiteChargeeActuelle: 0,
      statut: 'DISPONIBLE',
      immatriculation: `BENNE-${Date.now()}`
    };
    console.log('[v0] Sending to backend:', ressourceData);
    return this.http.post<Benne>(this.apiUrl, ressourceData).pipe(
      catchError(this.handleError)
    );
  }

  update(id: string, benne: Partial<Benne>): Observable<Benne> {
    // Convert field names for backend
    const updateData: any = { ...benne };
    if (benne.capaciteMax !== undefined) {
      updateData.capaciteKg = benne.capaciteMax;
      delete updateData.capaciteMax;
    }
    console.log('[v0] Updating benne with:', updateData);
    return this.http.put<Benne>(`${this.apiUrl}/${id}`, updateData).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  chargerBenne(id: string, quantite: number): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/charger`, { quantite }).pipe(
      catchError(this.handleError)
    );
  }

  viderBenne(id: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/vider`, {}).pipe(
      catchError(this.handleError)
    );
  }

  assignTracteur(benneId: string, tracteurId: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${benneId}/assign-tracteur/${tracteurId}`, {}).pipe(
      catchError(this.handleError)
    );
  }

  unassignTracteur(benneId: string): Observable<Benne> {
    return this.http.delete<Benne>(`${this.apiUrl}/${benneId}/unassign-tracteur`).pipe(
      catchError(this.handleError)
    );
  }

  startMaintenance(id: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/maintenance`, {}).pipe(
      catchError(this.handleError)
    );
  }

  endMaintenance(id: string): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/${id}/maintenance/end`, {}).pipe(
      catchError(this.handleError)
    );
  }

  getFullBennes(): Observable<Benne[]> {
    return this.http.get<Benne[]>(`${this.apiUrl}?type=BENNE&full=true`).pipe(
      catchError(this.handleError)
    );
  }

  getStats(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/stats`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur est survenue avec les bennes';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      errorMessage = `Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
