import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RessourceService, Tracteur } from '../../services/ressource';

@Component({
  selector: 'app-liste-tracteurs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './liste-tracteurs.html',
  styleUrls: ['./liste-tracteurs.css']
})
export class ListeTracteurs implements OnInit {
  tracteurs: Tracteur[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private ressourceService: RessourceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTracteurs();
  }

  loadTracteurs(): void {
    this.loading = true;
    this.errorMessage = '';
    this.ressourceService.getTracteurs().subscribe(
      (data: Tracteur[]) => {
        this.tracteurs = data;
        this.loading = false;
      },
      (error) => {
        this.errorMessage = 'Erreur lors du chargement des tracteurs';
        console.error('Error loading tracteurs:', error);
        this.loading = false;
      }
    );
  }

  ajouter(): void {
    this.router.navigate(['/ressources/tracteurs/creer']);
  }

  modifier(id: string | undefined): void {
    if (id) {
      this.router.navigate(['/ressources/tracteurs/modifier', id]);
    }
  }

  supprimer(id: string | undefined): void {
    if (id && confirm('Êtes-vous sûr de vouloir supprimer ce tracteur ?')) {
      this.ressourceService.deleteTracteur(id).subscribe(
        () => {
          this.loadTracteurs();
        },
        (error) => {
          this.errorMessage = 'Erreur lors de la suppression';
          console.error('Error deleting tracteur:', error);
        }
      );
    }
  }

  getStatusColor(status: string): string {
    switch (status?.toLowerCase()) {
      case 'disponible':
        return 'green';
      case 'en_utilisation':
        return 'blue';
      case 'maintenance':
        return 'orange';
      default:
        return 'gray';
    }
  }
}
