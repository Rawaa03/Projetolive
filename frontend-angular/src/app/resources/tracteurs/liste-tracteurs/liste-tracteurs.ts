import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { RouterModule } from '@angular/router';

import { TracteurService } from '../../../services/tracteur.service';
import { Tracteur } from '../../../models/tracteur';

@Component({
  selector: 'app-liste-tracteurs',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatSelectModule,
    MatChipsModule,
    MatProgressBarModule,
    RouterModule
  ],
  templateUrl: './liste-tracteurs.component.html',
  styleUrls: ['./liste-tracteurs.component.css']
})
export class ListeTracteursComponent implements OnInit {
  tracteurs: Tracteur[] = [];
  filteredTracteurs: Tracteur[] = [];
  loading = true;
  error: string | null = null;
  
  // Filtres
  searchText = '';
  statutFilter = '';
  carburantFilter = '';
  
  displayedColumns: string[] = ['id', 'marque', 'modele', 'puissanceMoteur', 'typeCarburant', 'kilometrage', 'statut', 'actions'];

  constructor(private tracteurService: TracteurService) {}

  ngOnInit(): void {
    this.loadTracteurs();
  }

  loadTracteurs(): void {
    this.loading = true;
    this.tracteurService.getAll().subscribe({
      next: (data) => {
        this.tracteurs = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des tracteurs';
        console.error(err);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredTracteurs = this.tracteurs.filter(tracteur => {
      const searchMatch = 
        (tracteur.id?.toLowerCase().includes(this.searchText.toLowerCase()) || false) ||
        (tracteur.marque?.toLowerCase().includes(this.searchText.toLowerCase()) || false) ||
        (tracteur.modele?.toLowerCase().includes(this.searchText.toLowerCase()) || false);
      
      const statutMatch = !this.statutFilter || tracteur.statut === this.statutFilter;
      const carburantMatch = !this.carburantFilter || tracteur.typeCarburant === this.carburantFilter;
      
      return searchMatch && statutMatch && carburantMatch;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  deleteTracteur(id: string | undefined): void {
    if (!id) return;
    
    if (confirm('Êtes-vous sûr de vouloir supprimer ce tracteur?')) {
      this.tracteurService.delete(id).subscribe({
        next: () => {
          this.loadTracteurs();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
          console.error(err);
        }
      });
    }
  }

  getStatutColor(statut: string): string {
    const colors: { [key: string]: string } = {
      'DISPONIBLE': 'success',
      'EN_USE': 'warning',
      'MAINTENANCE': 'info',
      'HORS_SERVICE': 'danger'
    };
    return colors[statut] || 'secondary';
  }

  getStatutIcon(statut: string): string {
    const icons: { [key: string]: string } = {
      'DISPONIBLE': 'check_circle',
      'EN_USE': 'directions_run',
      'MAINTENANCE': 'build',
      'HORS_SERVICE': 'cancel'
    };
    return icons[statut] || 'help';
  }

  getCarburantIcon(carburant: string): string {
    const icons: { [key: string]: string } = {
      'DIESEL': 'local_gas_station',
      'ESSENCE': 'local_gas_station',
      'ELECTRIQUE': 'electric_car'
    };
    return icons[carburant] || 'help';
  }
}
