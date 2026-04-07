import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RessourceService, Benne } from '../../services/ressource';

@Component({
  selector: 'app-creer-benne',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './creer-benne.html',
  styleUrls: ['./creer-benne.css']
})
export class CreerBenne implements OnInit {
  isEditing: boolean = false;
  benne: Benne = {
    type: '',
    statut: 'disponible',
    capacite: 0,
    poids_actuel: 0,
    pourcentage_remplissage: 0,
    usure: ''
  };
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  benneId: string | null = null;

  constructor(
    private ressourceService: RessourceService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditing = true;
        this.benneId = params['id'];
        this.loadBenne(params['id']);
      }
    });
  }

  loadBenne(id: string): void {
    this.loading = true;
    this.ressourceService.getBenne(id).subscribe(
      (data: Benne) => {
        this.benne = data;
        this.loading = false;
      },
      (error) => {
        this.errorMessage = 'Erreur lors du chargement de la benne';
        console.error('Error loading benne:', error);
        this.loading = false;
      }
    );
  }

  onSubmit(): void {
    if (!this.benne.type || this.benne.capacite <= 0) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.isEditing && this.benneId) {
      this.ressourceService.updateBenne(this.benneId, this.benne).subscribe(
        () => {
          this.successMessage = 'Benne modifiée avec succès';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/ressources/bennes']);
          }, 1500);
        },
        (error) => {
          this.errorMessage = 'Erreur lors de la modification';
          console.error('Error updating benne:', error);
          this.loading = false;
        }
      );
    } else {
      this.ressourceService.createBenne(this.benne).subscribe(
        () => {
          this.successMessage = 'Benne créée avec succès';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/ressources/bennes']);
          }, 1500);
        },
        (error) => {
          this.errorMessage = 'Erreur lors de la création';
          console.error('Error creating benne:', error);
          this.loading = false;
        }
      );
    }
  }

  goBack(): void {
    this.router.navigate(['/ressources/bennes']);
  }
}
