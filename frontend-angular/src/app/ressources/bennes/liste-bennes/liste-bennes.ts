import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BenneService } from '../../../services/benne.service';
import { Benne } from '../../../models/benne.model';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-liste-bennes',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './liste-bennes.html',
  styleUrls: ['./liste-bennes.css']
})
export class ListeBennesComponent implements OnInit {
  bennes: Benne[] = [];
  isLoading = true;
  errorMessage = '';
  selectedStatut: string = 'TOUS';
  selectedDisponibilite: string = 'TOUS';

  statuts: string[] = ['TOUS', 'DISPONIBLE', 'EN_USE', 'MAINTENANCE', 'HORS_SERVICE'];
  disponibilites: string[] = ['TOUS', 'DISPONIBLE', 'INDISPONIBLE'];

  constructor(private benneService: BenneService, private router: Router) {}

  ngOnInit(): void {
    this.loadBennes();
  }

  loadBennes(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.benneService.getAll().subscribe({
      next: (data: Benne[]) => {
        this.bennes = data;
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du chargement des bennes';
        this.isLoading = false;
        console.error('Erreur:', err);
      }
    });
  }

  filterBennes(): void {
    this.isLoading = true;
    this.benneService.getAll().subscribe({
      next: (data: Benne[]) => {
        let filteredBennes = [...data];

        if (this.selectedStatut !== 'TOUS') {
          filteredBennes = filteredBennes.filter(b => b.statut === this.selectedStatut);
        }

        if (this.selectedDisponibilite !== 'TOUS') {
          if (this.selectedDisponibilite === 'DISPONIBLE') {
            filteredBennes = filteredBennes.filter(b => b.statut === 'DISPONIBLE' && !b.tourneeId);
          } else {
            filteredBennes = filteredBennes.filter(b => b.statut !== 'DISPONIBLE' || b.tourneeId);
          }
        }

        this.bennes = filteredBennes;
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du filtrage des bennes';
        this.isLoading = false;
        console.error('Erreur:', err);
      }
    });
  }

  chargerBenne(id: string): void {
    const quantite = prompt('Entrez la quantité à charger (en kg):');
    if (quantite && !isNaN(Number(quantite))) {
      this.benneService.chargerBenne(id, Number(quantite)).subscribe({
        next: () => this.loadBennes(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors du chargement de la benne';
          console.error('Erreur:', err);
        }
      });
    }
  }

  viderBenne(id: string): void {
    if (confirm('Voulez-vous vraiment vider cette benne ?')) {
      this.benneService.viderBenne(id).subscribe({
        next: () => this.loadBennes(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors du vidage de la benne';
          console.error('Erreur:', err);
        }
      });
    }
  }

  startMaintenance(id: string): void {
    if (confirm('Mettre cette benne en maintenance ?')) {
      this.benneService.startMaintenance(id).subscribe({
        next: () => this.loadBennes(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la mise en maintenance';
          console.error('Erreur:', err);
        }
      });
    }
  }

  endMaintenance(id: string): void {
    if (confirm('Sortir cette benne de maintenance ?')) {
      this.benneService.endMaintenance(id).subscribe({
        next: () => this.loadBennes(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la sortie de maintenance';
          console.error('Erreur:', err);
        }
      });
    }
  }

  deleteBenne(id: string): void {
    if (confirm('Voulez-vous vraiment supprimer cette benne ?')) {
      this.benneService.delete(id).subscribe({
        next: () => this.loadBennes(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la suppression de la benne';
          console.error('Erreur:', err);
        }
      });
    }
  }

  navigateToAdd(): void {
    this.router.navigate(['/ressources/bennes/ajouter']);
  }

  navigateToEdit(id: string): void {
    this.router.navigate([`/ressources/bennes/modifier/${id}`]);
  }

  getTourneeIdDisplay(tourneeId: string | undefined): string {
    return tourneeId ? tourneeId.substring(0, 6) : '';
  }
}
