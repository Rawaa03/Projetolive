export interface Tracteur {
  id?: string;
  puissanceMoteur: number;
  consommationCarburant: number;
  kilometrage: number;
  typeCarburant: 'DIESEL' | 'ESSENCE' | 'ELECTRIQUE' | 'HYBRIDE';
  remorqueAttachee: boolean;
  marque: string;
  modele: string;
  annee: number;
  couleur?: string;
  description?: string;
  statut: 'DISPONIBLE' | 'EN_USE' | 'MAINTENANCE' | 'HORS_SERVICE';
  operateurAssigne?: string;
  tourneeId?: string;
  historiqueEntretien?: MaintenanceRecord[];
  dateCreation?: string;
  dateModification?: string;
}

export interface TracteurCreation {
  puissanceMoteur: number;
  consommationCarburant: number;
  kilometrage: number;
  typeCarburant: 'DIESEL' | 'ESSENCE' | 'ELECTRIQUE' | 'HYBRIDE';
  remorqueAttachee: boolean;
  marque: string;
  modele: string;
  annee: number;
  couleur?: string;
  description?: string;
}

export interface MaintenanceRecord {
  date: string;
  description: string;
  cout?: number;
}
