import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Benne {
  _id?: string;
  type: string;
  statut: string;
  capacite: number;
  poids_actuel: number;
  pourcentage_remplissage: number;
  tracteur_assigne?: string;
  historique_maintenance: Array<{
    date_debut: string;
    date_fin?: string;
    type_maintenance: string;
    description: string;
  }>;
  usure: string;
}

export interface Tracteur {
  _id?: string;
  type: string;
  statut: string;
  marque: string;
  modele: string;
  annee: number;
  moteur_specifications: {
    type_carburant: string;
    cylindree: number;
    puissance_cv: number;
  };
  consommation_carburant: number;
  kilometrage: number;
  conducteur_assigne?: string;
  historique_maintenance: Array<{
    date_debut: string;
    date_fin?: string;
    type_maintenance: string;
    description: string;
  }>;
}

@Injectable({
  providedIn: 'root'
})
export class RessourceService {
  private apiUrl = 'http://localhost:8080/api/ressources';

  constructor(private http: HttpClient) {}

  // Bennes
  getBennes(): Observable<Benne[]> {
    return this.http.get<Benne[]>(`${this.apiUrl}/bennes`);
  }

  getBenne(id: string): Observable<Benne> {
    return this.http.get<Benne>(`${this.apiUrl}/bennes/${id}`);
  }

  createBenne(benne: Benne): Observable<Benne> {
    return this.http.post<Benne>(`${this.apiUrl}/bennes`, benne);
  }

  updateBenne(id: string, benne: Benne): Observable<Benne> {
    return this.http.put<Benne>(`${this.apiUrl}/bennes/${id}`, benne);
  }

  deleteBenne(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/bennes/${id}`);
  }

  // Tracteurs
  getTracteurs(): Observable<Tracteur[]> {
    return this.http.get<Tracteur[]>(`${this.apiUrl}/tracteurs`);
  }

  getTracteur(id: string): Observable<Tracteur> {
    return this.http.get<Tracteur>(`${this.apiUrl}/tracteurs/${id}`);
  }

  createTracteur(tracteur: Tracteur): Observable<Tracteur> {
    return this.http.post<Tracteur>(`${this.apiUrl}/tracteurs`, tracteur);
  }

  updateTracteur(id: string, tracteur: Tracteur): Observable<Tracteur> {
    return this.http.put<Tracteur>(`${this.apiUrl}/tracteurs/${id}`, tracteur);
  }

  deleteTracteur(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tracteurs/${id}`);
  }
}
