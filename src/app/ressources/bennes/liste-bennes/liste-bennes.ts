import { Component, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BenneService } from '../../../services/benne';
import { Benne } from '../../../models/benne';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { SideBarResponsable } from '../../../sidebar-responsable/sidebar-responsable';

@Component({
  selector: 'app-liste-bennes',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SideBarResponsable],
  templateUrl: './liste-bennes.html',
  styleUrls: ['./liste-bennes.css']
})
export class ListeBennesComponent implements OnInit {
  bennes: Benne[] = [];
  isLoading = true;
  errorMessage = '';
  selectedStatut: string = 'TOUS';
  filteredBennes: Benne[] = [];

  // Propriétés pour la sidebar
  isSidebarCollapsed = false;
  isMobile = false;
  userRole: string = '';

  statuts: string[] = ['TOUS', 'DISPONIBLE', 'EN_USE', 'MAINTENANCE', 'HORS_SERVICE'];
  successMessage: string = '';
  deleteConfirmId: string | null = null;

  constructor(private benneService: BenneService, public router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadUserRole();
    this.checkMobile();
    this.loadBennes();
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

  loadBennes(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.benneService.getAll().subscribe({
      next: (data: Benne[]) => {
        this.bennes = data || [];
        this.applyFilters();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du chargement des bennes';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters(): void {
    this.filteredBennes = this.bennes.filter(benne => {
      if (this.selectedStatut !== 'TOUS' && benne.statut !== this.selectedStatut) {
        return false;
      }
      return true;
    });
  }

  onFilterChange(): void {
    this.applyFilters();
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
    this.deleteConfirmId = id;
  }

  confirmDelete(): void {
    if (this.deleteConfirmId) {
      this.benneService.delete(this.deleteConfirmId).subscribe({
        next: () => {
          this.successMessage = 'Benne supprimée avec succès';
          this.deleteConfirmId = null;
          setTimeout(() => this.loadBennes(), 500);
        },
        error: (err: HttpErrorResponse) => {
          this.errorMessage = err.message || 'Erreur lors de la suppression';
          this.deleteConfirmId = null;
        }
      });
    }
  }

  cancelDelete(): void {
    this.deleteConfirmId = null;
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
