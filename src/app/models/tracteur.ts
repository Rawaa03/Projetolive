import { Ressource, RessourceType, RessourceStatut } from './ressource';

export interface Tracteur extends Ressource {
  type: 'TRACTEUR';
  puissanceMoteur: number;
  consommationCarburant: number;
  kilometrage: number;
  conducteurId?: string;
  remorqueAttachee: boolean;
  dateDernierPlein?: Date;
  typeCarburant: string;
  marque?: string;
  modele?: string;
  annee?: number;
  couleur?: string;
  description?: string;
}

// Interface pour la création (sans les propriétés calculées ou générées par le backend)
export interface TracteurCreation {
  puissanceMoteur: number;
  consommationCarburant: number;
  kilometrage: number;
  typeCarburant: string;
  remorqueAttachee: boolean;
  marque?: string;
  modele?: string;
  annee?: number;
  couleur?: string;
  description?: string;
}