import { Ressource, RessourceType, RessourceStatut } from './ressource';

export interface Tracteur extends Ressource {
  type: 'TRACTEUR';
  puissance?: string;
  carburant?: string;
  consommationHoraire?: number;
  aRemorque?: boolean;
  kilometrage?: number;
  conducteurId?: string;
}

// Interface pour la création (sans les propriétés calculées ou générées par le backend)
export interface TracteurCreation {
  nom: string;
  immatriculation: string;
  puissance?: string;
  carburant?: string;
  consommationHoraire?: number;
  aRemorque?: boolean;
  kilometrage?: number;
}
