// src/app/ressources/tracteurs/modifier-tracteur/modifier-tracteur.ts
import { Component, OnInit, HostListener, ChangeDetectorRef } from '@angular/core';
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
  
  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.tracteurForm = this.fb.group({
      statut: ['DISPONIBLE', Validators.required]
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
          statut: data.statut || 'DISPONIBLE'
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du chargement du tracteur';
        this.isLoading = false;
        this.cdr.detectChanges();
        console.error('Erreur:', err);
      }
    });
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

    const tracteurData: Partial<Tracteur> = {
      statut: this.tracteurForm.value.statut
    };

    this.tracteurService.update(this.tracteurId, tracteurData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = 'Tracteur modifié avec succès !';
        this.cdr.detectChanges();
        setTimeout(() => {
          this.router.navigate(['/ressources/tracteurs']);
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Erreur lors de la modification du tracteur';
        this.cdr.detectChanges();
        console.error('Erreur:', err);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/ressources/tracteurs']);
  }
}
