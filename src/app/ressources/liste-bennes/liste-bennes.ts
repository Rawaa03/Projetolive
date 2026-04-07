import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RessourceService, Benne } from '../../services/ressource';

@Component({
  selector: 'app-liste-bennes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './liste-bennes.html',
  styleUrls: ['./liste-bennes.css']
})
export class ListeBennes implements OnInit {
  bennes: Benne[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private ressourceService: RessourceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBennes();
  }

  loadBennes(): void {
    this.loading = true;
    this.errorMessage = '';
    this.ressourceService.getBennes().subscribe(
      (data: Benne[]) => {
        this.bennes = data;
        this.loading = false;
      },
      (error) => {
        this.errorMessage = 'Erreur lors du chargement des bennes';
        console.error('Error loading bennes:', error);
        this.loading = false;
      }
    );
  }

  ajouter(): void {
    this.router.navigate(['/ressources/bennes/creer']);
  }

  modifier(id: string | undefined): void {
    if (id) {
      this.router.navigate(['/ressources/bennes/modifier', id]);
    }
  }

  supprimer(id: string | undefined): void {
    if (id && confirm('Êtes-vous sûr de vouloir supprimer cette benne ?')) {
      this.ressourceService.deleteBenne(id).subscribe(
        () => {
          this.loadBennes();
        },
        (error) => {
          this.errorMessage = 'Erreur lors de la suppression';
          console.error('Error deleting benne:', error);
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
