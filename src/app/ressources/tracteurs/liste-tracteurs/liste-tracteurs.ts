import { Component, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TracteurService } from '../../../services/tracteur';
import { Tracteur } from '../../../models/tracteur';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { SideBarResponsable } from '../../../sidebar-responsable/sidebar-responsable';

@Component({
  selector: 'app-liste-tracteurs',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SideBarResponsable],
  templateUrl: './liste-tracteurs.html',
  styleUrls: ['./liste-tracteurs.css']
})
export class ListeTracteursComponent implements OnInit {
  tracteurs: Tracteur[] = [];
  isLoading = true;
  errorMessage = '';
  selectedStatut: string = 'TOUS';
  selectedDisponibilite: string = 'TOUS';
  selectedCarburant: string = 'TOUS';

  // Propriétés pour la sidebar
  isSidebarCollapsed = false;
  isMobile = false;
  userRole: string = '';

  statuts: string[] = ['TOUS', 'DISPONIBLE', 'EN_USE', 'MAINTENANCE', 'HORS_SERVICE'];
  disponibilites: string[] = ['TOUS', 'DISPONIBLE', 'INDISPONIBLE'];
  carburants: string[] = ['TOUS', 'DIESEL', 'ESSENCE', 'ELECTRIQUE', 'HYBRIDE'];

  constructor(private tracteurService: TracteurService, public router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadUserRole();
    this.checkMobile();
    this.loadTracteurs();
  }

  loadUserRole(): void {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        this.userRole = user.role?.toUpperCase() || '';
      } catch (e) {
        console.error('Error parsing user data', e);
      }
    }
  }

  @HostListener('window:resize')
  checkMobile(): void {
    this.isMobile = window.innerWidth <= 768;
    if (!this.isMobile) {
      this.isSidebarCollapsed = false;
    }
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  loadTracteurs(): void {
    this.isLoading = true;
    this.tracteurService.getAll().subscribe({
      next: (data: Tracteur[]) => {
        this.tracteurs = data || [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du chargement des tracteurs';
        this.isLoading = false;
        this.cdr.detectChanges();
        console.error('Erreur:', err);
      }
    });
  }

  filterTracteurs(): void {
    this.isLoading = true;
    this.tracteurService.getAll().subscribe({
      next: (data: Tracteur[]) => {
        let filteredTracteurs = [...data];

        if (this.selectedStatut !== 'TOUS') {
          filteredTracteurs = filteredTracteurs.filter(t => t.statut === this.selectedStatut);
        }

        if (this.selectedDisponibilite !== 'TOUS') {
          if (this.selectedDisponibilite === 'DISPONIBLE') {
            filteredTracteurs = filteredTracteurs.filter(t => t.statut === 'DISPONIBLE' && !t.tourneeId);
          } else {
            filteredTracteurs = filteredTracteurs.filter(t => t.statut !== 'DISPONIBLE' || t.tourneeId);
          }
        }

        if (this.selectedCarburant !== 'TOUS') {
          filteredTracteurs = filteredTracteurs.filter(t => t.typeCarburant === this.selectedCarburant);
        }

        this.tracteurs = filteredTracteurs;
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du filtrage des tracteurs';
        this.isLoading = false;
        console.error('Erreur:', err);
      }
    });
  }

  updateKilometrage(id: string): void {
    const kilometrage = prompt('Entrez le nouveau kilométrage:');
    if (kilometrage && !isNaN(Number(kilometrage))) {
      this.tracteurService.updateKilometrage(id, Number(kilometrage)).subscribe({
        next: () => this.loadTracteurs(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la mise à jour du kilométrage';
          console.error('Erreur:', err);
        }
      });
    }
  }

  estimateConsumption(id: string): void {
    const distance = prompt('Entrez la distance (en km) pour estimer la consommation:');
    if (distance && !isNaN(Number(distance))) {
      this.tracteurService.estimateConsumption(id, Number(distance)).subscribe({
        next: (consumption: number) => {
          alert(`Consommation estimée: ${consumption.toFixed(2)} litres`);
        },
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de l\'estimation de la consommation';
          console.error('Erreur:', err);
        }
      });
    }
  }

  startMaintenance(id: string): void {
    if (confirm('Mettre ce tracteur en maintenance ?')) {
      this.tracteurService.startMaintenance(id).subscribe({
        next: () => this.loadTracteurs(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la mise en maintenance';
          console.error('Erreur:', err);
        }
      });
    }
  }

  endMaintenance(id: string): void {
    if (confirm('Sortir ce tracteur de maintenance ?')) {
      this.tracteurService.endMaintenance(id).subscribe({
        next: () => this.loadTracteurs(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la sortie de maintenance';
          console.error('Erreur:', err);
        }
      });
    }
  }

  toggleTrailer(id: string, tracteur: Tracteur): void {
    const newStatus = !tracteur.remorqueAttachee;
    const updatedTracteur: Partial<Tracteur> = { remorqueAttachee: newStatus };
    this.tracteurService.update(id, updatedTracteur).subscribe({
      next: () => this.loadTracteurs(),
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors de la mise à jour de la remorque';
        console.error('Erreur:', err);
      }
    });
  }

  deleteTracteur(id: string): void {
    if (confirm('Voulez-vous vraiment supprimer ce tracteur ?')) {
      this.tracteurService.delete(id).subscribe({
        next: () => this.loadTracteurs(),
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la suppression du tracteur';
          console.error('Erreur:', err);
        }
      });
    }
  }

  navigateToAdd(): void {
    this.router.navigate(['/ressources/tracteurs/ajouter']);
  }

  navigateToEdit(id: string): void {
    this.router.navigate([`/ressources/tracteurs/modifier/${id}`]);
  }

  calculateFuelLevel(tracteur: Tracteur): number {
    const reservoirCapacity = 100;
    const consommationMoyenne = tracteur.consommationCarburant || 6;
    const autonomie = reservoirCapacity / (consommationMoyenne / 100);
    const fuelLevel = 100 - (tracteur.kilometrage % autonomie) / autonomie * 100;
    return Math.max(0, Math.min(100, Math.round(fuelLevel)));
  }

  getTourneeIdDisplay(tourneeId: string | undefined): string {
    return tourneeId ? tourneeId.substring(0, 6) : '';
  }
}
