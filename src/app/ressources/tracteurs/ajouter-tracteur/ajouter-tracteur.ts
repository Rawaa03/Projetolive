// src/app/ressources/tracteurs/ajouter-tracteur/ajouter-tracteur.ts
import { Component, OnInit, HostListener, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TracteurService } from '../../../services/tracteur';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TracteurCreation } from '../../../models/tracteur';
import { SideBarResponsable } from '../../../sidebar-responsable/sidebar-responsable';

@Component({
  selector: 'app-ajouter-tracteur',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    SideBarResponsable
  ],
  templateUrl: './ajouter-tracteur.html',
  styleUrls: ['./ajouter-tracteur.css']
})
export class AjouterTracteurComponent implements OnInit {
  tracteurForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Propriétés pour la sidebar
  isSidebarCollapsed = false;
  isMobile = false;
  userRole: string = '';
  
  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.tracteurForm = this.fb.group({
      nom: ['', Validators.required],
      immatriculation: ['', Validators.required],
      puissance: [''],
      carburant: [''],
      consommationHoraire: [''],
      kilometrage: [''],
      aRemorque: [false]
    });
  }

  ngOnInit(): void {
    this.loadUserRole();
    this.checkMobile();
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

  onSubmit(): void {
    if (this.tracteurForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      this.cdr.detectChanges();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const tracteurData: TracteurCreation = {
      nom: this.tracteurForm.value.nom,
      immatriculation: this.tracteurForm.value.immatriculation,
      puissance: this.tracteurForm.value.puissance || '',
      carburant: this.tracteurForm.value.carburant || '',
      consommationHoraire: this.tracteurForm.value.consommationHoraire ? Number(this.tracteurForm.value.consommationHoraire) : undefined,
      kilometrage: this.tracteurForm.value.kilometrage ? Number(this.tracteurForm.value.kilometrage) : undefined,
      aRemorque: this.tracteurForm.value.aRemorque || false
    };

    this.tracteurService.create(tracteurData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = 'Tracteur ajouté avec succès !';
        this.cdr.detectChanges();
        setTimeout(() => {
          this.router.navigate(['/ressources/tracteurs']);
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Erreur lors de l\'ajout du tracteur';
        this.cdr.detectChanges();
        console.error('Erreur:', err);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/ressources/tracteurs']);
  }
}
