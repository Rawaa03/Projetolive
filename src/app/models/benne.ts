import { Ressource, RessourceType, RessourceStatut } from './ressource';

export interface Benne extends Ressource {
  type: 'BENNE';
  capaciteKg: number;
  quantiteChargeeActuelle?: number;
  tauxRemplissage?: number;
  estPleine?: boolean;
  tracteurAttacheId?: string;
}

// Interface pour la création (sans les propriétés calculées ou générées par le backend)
export interface BenneCreation {
  nom: string;
  immatriculation: string;
  capaciteKg: number;
}
