// src/app/ressources/bennes/modifier-benne/modifier-benne.ts
import { Component, OnInit, HostListener, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BenneService } from '../../../services/benne';
import { ActivatedRoute, Router } from '@angular/router';
import { Benne } from '../../../models/benne';
import { HttpErrorResponse } from '@angular/common/http';
import { SideBarResponsable } from '../../../sidebar-responsable/sidebar-responsable';

@Component({
  selector: 'app-modifier-benne',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    SideBarResponsable
  ],
  templateUrl: './modifier-benne.html',
  styleUrls: ['./modifier-benne.css']
})
export class ModifierBenneComponent implements OnInit {
  benneForm: FormGroup;
  isLoading = true;
  errorMessage = '';
  successMessage = '';
  benneId: string = '';
  benne: Benne | undefined;

  // Propriétés pour la sidebar
  isSidebarCollapsed = false;
  isMobile = false;
  userRole: string = '';

  constructor(
    private fb: FormBuilder,
    private benneService: BenneService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.benneForm = this.fb.group({
      statut: ['DISPONIBLE', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUserRole();
    this.checkMobile();
    this.benneId = this.route.snapshot.params['id'];
    this.loadBenne();
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

  loadBenne(): void {
    this.isLoading = true;
    this.benneService.getById(this.benneId).subscribe({
      next: (data: Benne) => {
        this.benne = data;
        this.benneForm.patchValue({
          statut: data.statut || 'DISPONIBLE'
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Erreur lors du chargement de la benne';
        this.isLoading = false;
        this.cdr.detectChanges();
        console.error('Erreur:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.benneForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      this.cdr.detectChanges();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const benneData: Partial<Benne> = {
      statut: this.benneForm.value.statut
    };

    this.benneService.update(this.benneId, benneData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = 'Benne modifiée avec succès !';
        this.cdr.detectChanges();
        setTimeout(() => {
          this.router.navigate(['/ressources/bennes']);
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Erreur lors de la modification de la benne';
        this.cdr.detectChanges();
        console.error('Erreur:', err);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/ressources/bennes']);
  }
}
