import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RessourceService, Tracteur } from '../../services/ressource';

@Component({
  selector: 'app-creer-tracteur',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './creer-tracteur.html',
  styleUrls: ['./creer-tracteur.css']
})
export class CreerTracteur implements OnInit {
  isEditing: boolean = false;
  tracteur: Tracteur = {
    type: '',
    statut: 'disponible',
    marque: '',
    modele: '',
    annee: new Date().getFullYear(),
    moteur_specifications: {
      type_carburant: '',
      cylindree: 0,
      puissance_cv: 0
    },
    consommation_carburant: 0,
    kilometrage: 0
  };
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  tracteurId: string | null = null;

  constructor(
    private ressourceService: RessourceService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditing = true;
        this.tracteurId = params['id'];
        this.loadTracteur(params['id']);
      }
    });
  }

  loadTracteur(id: string): void {
    this.loading = true;
    this.ressourceService.getTracteur(id).subscribe(
      (data: Tracteur) => {
        this.tracteur = data;
        this.loading = false;
      },
      (error) => {
        this.errorMessage = 'Erreur lors du chargement du tracteur';
        console.error('Error loading tracteur:', error);
        this.loading = false;
      }
    );
  }

  onSubmit(): void {
    if (!this.tracteur.type || !this.tracteur.marque || !this.tracteur.modele) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.isEditing && this.tracteurId) {
      this.ressourceService.updateTracteur(this.tracteurId, this.tracteur).subscribe(
        () => {
          this.successMessage = 'Tracteur modifié avec succès';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/ressources/tracteurs']);
          }, 1500);
        },
        (error) => {
          this.errorMessage = 'Erreur lors de la modification';
          console.error('Error updating tracteur:', error);
          this.loading = false;
        }
      );
    } else {
      this.ressourceService.createTracteur(this.tracteur).subscribe(
        () => {
          this.successMessage = 'Tracteur créé avec succès';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/ressources/tracteurs']);
          }, 1500);
        },
        (error) => {
          this.errorMessage = 'Erreur lors de la création';
          console.error('Error creating tracteur:', error);
          this.loading = false;
        }
      );
    }
  }

  goBack(): void {
    this.router.navigate(['/ressources/tracteurs']);
  }
}
