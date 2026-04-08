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
  
  // Année actuelle pour les validators
  currentYear = new Date().getFullYear();

  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.tracteurForm = this.fb.group({
      puissanceMoteur: ['', [Validators.required, Validators.min(1)]],
      consommationCarburant: ['', [Validators.required, Validators.min(0.1)]],
      kilometrage: [0, [Validators.required, Validators.min(0)]],
      typeCarburant: ['', Validators.required],
      remorqueAttachee: [false],
      marque: [''],
      modele: [''],
      annee: ['', [Validators.min(1990), Validators.max(this.currentYear)]],
      couleur: ['#5A6E1A'],
      description: ['']
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
      puissanceMoteur: Number(this.tracteurForm.value.puissanceMoteur),
      consommationCarburant: Number(this.tracteurForm.value.consommationCarburant),
      kilometrage: Number(this.tracteurForm.value.kilometrage),
      typeCarburant: this.tracteurForm.value.typeCarburant,
      remorqueAttachee: this.tracteurForm.value.remorqueAttachee || false,
      marque: this.tracteurForm.value.marque,
      modele: this.tracteurForm.value.modele,
      annee: Number(this.tracteurForm.value.annee),
      couleur: this.tracteurForm.value.couleur || '',
      description: this.tracteurForm.value.description || ''
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
