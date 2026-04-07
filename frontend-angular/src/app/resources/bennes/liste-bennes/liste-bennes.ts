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

import { BenneService } from '../../../services/benne.service';
import { Benne } from '../../../models/benne';

@Component({
  selector: 'app-liste-bennes',
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
  templateUrl: './liste-bennes.component.html',
  styleUrls: ['./liste-bennes.component.css']
})
export class ListeBennesComponent implements OnInit {
  bennes: Benne[] = [];
  filteredBennes: Benne[] = [];
  loading = true;
  error: string | null = null;
  
  // Filtres
  searchText = '';
  statutFilter = '';
  materiauxFilter = '';
  
  displayedColumns: string[] = ['id', 'capaciteMax', 'chargeActuelle', 'pourcentageRemplissage', 'statut', 'typeMateriau', 'actions'];

  constructor(private benneService: BenneService) {}

  ngOnInit(): void {
    this.loadBennes();
  }

  loadBennes(): void {
    this.loading = true;
    this.benneService.getAll().subscribe({
      next: (data) => {
        this.bennes = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des bennes';
        console.error(err);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredBennes = this.bennes.filter(benne => {
      const searchMatch = 
        (benne.id?.toLowerCase().includes(this.searchText.toLowerCase()) || false) ||
        (benne.typeMateriau?.toLowerCase().includes(this.searchText.toLowerCase()) || false);
      
      const statutMatch = !this.statutFilter || benne.statut === this.statutFilter;
      const materiauxMatch = !this.materiauxFilter || benne.typeMateriau === this.materiauxFilter;
      
      return searchMatch && statutMatch && materiauxMatch;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  deleteBenne(id: string | undefined): void {
    if (!id) return;
    
    if (confirm('Êtes-vous sûr de vouloir supprimer cette benne?')) {
      this.benneService.delete(id).subscribe({
        next: () => {
          this.loadBennes();
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
}
