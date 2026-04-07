import { Ressource, RessourceType, RessourceStatut } from './ressource';

export interface Benne extends Ressource {
  type: 'BENNE';
  capaciteMax: number;
  chargeActuelle: number;
  pourcentageRemplissage: number;
  typeMateriau: string;
  tracteurId?: string;
  dateDernierChargement?: Date;
  dateDerniereVidange?: Date;
  usure: number;
  couleur?: string;
  description?: string;
}

// Interface pour la création (sans les propriétés calculées ou générées par le backend)
export interface BenneCreation {
  capaciteMax: number;
  typeMateriau: string;
  usure: number;
  couleur?: string;
  description?: string;
}