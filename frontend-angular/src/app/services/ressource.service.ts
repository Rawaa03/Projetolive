import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Ressource, RessourceType, RessourceStatut } from '../models/ressource';

@Injectable({
  providedIn: 'root'
})
export class RessourceService {
  private apiUrl = 'http://localhost:8080/api/ressources';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: string): Observable<Ressource> {
    return this.http.get<Ressource>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  create(ressource: Partial<Ressource>): Observable<Ressource> {
    return this.http.post<Ressource>(this.apiUrl, ressource).pipe(
      catchError(this.handleError)
    );
  }

  update(id: string, ressource: Partial<Ressource>): Observable<Ressource> {
    return this.http.put<Ressource>(`${this.apiUrl}/${id}`, ressource).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getByType(type: RessourceType): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(`${this.apiUrl}/type/${type}`).pipe(
      catchError(this.handleError)
    );
  }

  getByStatut(statut: RessourceStatut): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(`${this.apiUrl}/statut/${statut}`).pipe(
      catchError(this.handleError)
    );
  }

  getAvailable(): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(`${this.apiUrl}/available`).pipe(
      catchError(this.handleError)
    );
  }

  checkAvailability(id: string, startDate: string, endDate: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${id}/available?startDate=${startDate}&endDate=${endDate}`).pipe(
      catchError(this.handleError)
    );
  }

  assignToTournee(ressourceId: string, tourneeId: string): Observable<Ressource> {
    return this.http.post<Ressource>(`${this.apiUrl}/${ressourceId}/assign-tour/${tourneeId}`, {}).pipe(
      catchError(this.handleError)
    );
  }

  unassignFromTournee(ressourceId: string): Observable<Ressource> {
    return this.http.delete<Ressource>(`${this.apiUrl}/${ressourceId}/unassign-tour`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur est survenue';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      errorMessage = `Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}