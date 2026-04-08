import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Tracteur, TracteurCreation } from '../models/tracteur';

@Injectable({
  providedIn: 'root'
})
export class TracteurService {
  private apiUrl = 'http://localhost:8080/api/ressources/tracteurs';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: string): Observable<Tracteur> {
    return this.http.get<Tracteur>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  create(tracteur: TracteurCreation): Observable<Tracteur> {
    const ressourceData = {
      type: 'TRACTEUR',
      nom: `${tracteur.marque} ${tracteur.modele}`,
      marque: tracteur.marque,
      modele: tracteur.modele,
      puissance: String(tracteur.puissanceMoteur),
      carburant: tracteur.typeCarburant,
      consommationHoraire: tracteur.consommationCarburant,
      kilometrage: tracteur.kilometrage,
      aRemorque: tracteur.remorqueAttachee,
      statut: 'DISPONIBLE',
      immatriculation: `TRAC-${Date.now()}`
    };
    return this.http.post<Tracteur>(this.apiUrl, ressourceData).pipe(
      catchError(this.handleError)
    );
  }

  update(id: string, tracteur: Partial<Tracteur>): Observable<Tracteur> {
    const updateData: any = {};
    if (tracteur.puissanceMoteur !== undefined) {
      updateData.puissance = String(tracteur.puissanceMoteur);
    }
    if (tracteur.typeCarburant !== undefined) {
      updateData.carburant = tracteur.typeCarburant;
    }
    if (tracteur.consommationCarburant !== undefined) {
      updateData.consommationHoraire = tracteur.consommationCarburant;
    }
    if (tracteur.remorqueAttachee !== undefined) {
      updateData.aRemorque = tracteur.remorqueAttachee;
    }
    if (tracteur.statut !== undefined) {
      updateData.statut = tracteur.statut;
    }
    if (tracteur.kilometrage !== undefined) {
      updateData.kilometrage = tracteur.kilometrage;
    }
    return this.http.put<Tracteur>(`${this.apiUrl}/${id}`, updateData).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getAvailable(): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(`${this.apiUrl}/available`).pipe(
      catchError(this.handleError)
    );
  }

  getSpecs(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/specs`).pipe(
      catchError(this.handleError)
    );
  }

  updateKilometrage(id: string, kilometrage: number): Observable<Tracteur> {
    return this.http.put<Tracteur>(`${this.apiUrl}/${id}`, { kilometrage }).pipe(
      catchError(this.handleError)
    );
  }

  estimateConsumption(id: string, distance: number): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/${id}/consumption-estimate`, { distance }).pipe(
      catchError(this.handleError)
    );
  }

  assignConducteur(tracteurId: string, conducteurId: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${tracteurId}/assign-driver/${conducteurId}`, {}).pipe(
      catchError(this.handleError)
    );
  }

  unassignConducteur(tracteurId: string): Observable<Tracteur> {
    return this.http.delete<Tracteur>(`${this.apiUrl}/${tracteurId}/unassign-driver`).pipe(
      catchError(this.handleError)
    );
  }

  startMaintenance(id: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/maintenance`, {}).pipe(
      catchError(this.handleError)
    );
  }

  endMaintenance(id: string): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/${id}/maintenance/end`, {}).pipe(
      catchError(this.handleError)
    );
  }

  getWithTrailer(): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(`${this.apiUrl}/with-trailer`).pipe(
      catchError(this.handleError)
    );
  }

  getStats(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/stats`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur est survenue avec les tracteurs';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      errorMessage = `Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
