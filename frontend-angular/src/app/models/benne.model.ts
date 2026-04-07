export interface Benne {
  id?: string;
  capaciteMax: number;
  chargeActuelle: number;
  typeMateriau: string;
  usure: number;
  couleur?: string;
  description?: string;
  statut: 'DISPONIBLE' | 'EN_USE' | 'MAINTENANCE' | 'HORS_SERVICE';
  tourneeId?: string;
  historiqueMaintenance?: MaintenanceRecord[];
  tracteurAssigne?: string;
  dateCreation?: string;
  dateModification?: string;
}

export interface BenneCreation {
  capaciteMax: number;
  typeMateriau: string;
  usure: number;
  couleur?: string;
  description?: string;
}

export interface MaintenanceRecord {
  date: string;
  description: string;
  cout?: number;
}
