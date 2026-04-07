// src/app/ressources/tracteurs/modifier-tracteur/modifier-tracteur.ts
import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TracteurService } from '../../../services/tracteur';
import { ActivatedRoute, Router } from '@angular/router';
import { Tracteur } from '../../../models/tracteur';
import { HttpErrorResponse } from '@angular/common/http';
import { SideBarResponsable } from '../../../sidebar-responsable/sidebar-responsable';

@Component({
  selector: 'app-modifier-tracteur',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    SideBarResponsable
  ],
  templateUrl: './modifier-tracteur.html',
  styleUrls: ['./modifier-tracteur.css']
})
export class ModifierTracteurComponent implements OnInit {
  tracteurForm: FormGroup;
  isLoading = true;
  errorMessage = '';
  successMessage = '';
  tracteurId: string = '';
  tracteur: Tracteur | undefined;

  // Propriétés pour la sidebar
  isSidebarCollapsed = false;
  isMobile = false;
  userRole: string = '';
  
  // Année actuelle pour les validators
  currentYear = new Date().getFullYear();

  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
    private route: ActivatedRoute,
    private router: Router
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
      description: [''],
      enMaintenance: [false]
    });
  }

  ngOnInit(): void {
    this.loadUserRole();
    this.checkMobile();
    this.tracteurId = this.route.snapshot.params['id'];
    this.loadTracteur();
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

  loadTracteur(): void {
    this.isLoading = true;
    this.tracteurService.getById(this.tracteurId).subscribe({
      next: (data: Tracteur) => {
        this.tracteur = data;
        this.tracteurForm.patchValue({
          puissanceMoteur: data.puissanceMoteur,
          consommationCarburant: data.consommationCarburant,
          kilometrage: data.kilometrage,
          typeCarburant: data.typeCarburant,
          remorqueAttachee: data.remorqueAttachee,
          marque: data.marque || '',
          modele: data.modele || '',
          annee: data.annee || '',
          couleur: data.couleur || '#5A6E1A',
          description: data.description || '',
          enMaintenance: data.statut === 'MAINTENANCE'
        });
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du chargement du tracteur';
        this.isLoading = false;
        console.error('Erreur:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.tracteurForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const tracteurData: Partial<Tracteur> = {
      puissanceMoteur: Number(this.tracteurForm.value.puissanceMoteur),
      consommationCarburant: Number(this.tracteurForm.value.consommationCarburant),
      kilometrage: Number(this.tracteurForm.value.kilometrage),
      typeCarburant: this.tracteurForm.value.typeCarburant,
      remorqueAttachee: this.tracteurForm.value.remorqueAttachee || false,
      marque: this.tracteurForm.value.marque,
      modele: this.tracteurForm.value.modele,
      annee: Number(this.tracteurForm.value.annee),
      couleur: this.tracteurForm.value.couleur || '',
      description: this.tracteurForm.value.description || '',
      statut: this.tracteurForm.value.enMaintenance ? 'MAINTENANCE' : 'DISPONIBLE'
    };

    console.log('[v0] Updating tracteur with data:', tracteurData);

    this.tracteurService.update(this.tracteurId, tracteurData).subscribe({
      next: (response) => {
        console.log('[v0] Tracteur updated successfully:', response);
        this.isLoading = false;
        this.successMessage = 'Tracteur modifié avec succès !';
        setTimeout(() => {
          this.router.navigate(['/ressources/tracteurs']);
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
        console.log('[v0] Error updating tracteur:', err);
        this.isLoading = false;
        this.errorMessage = err.message || 'Erreur lors de la modification du tracteur';
        console.error('Erreur:', err);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/ressources/tracteurs']);
  }
}
