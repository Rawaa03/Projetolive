import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TracteurService } from '../../../services/tracteur.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Tracteur } from '../../../models/tracteur.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-modifier-tracteur',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
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
  carburants: string[] = ['DIESEL', 'ESSENCE', 'ELECTRIQUE', 'HYBRIDE'];

  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    const currentYear = new Date().getFullYear();
    this.tracteurForm = this.fb.group({
      puissanceMoteur: ['', [Validators.required, Validators.min(1)]],
      consommationCarburant: ['', [Validators.required, Validators.min(0.1)]],
      kilometrage: [0, [Validators.required, Validators.min(0)]],
      typeCarburant: ['', Validators.required],
      remorqueAttachee: [false],
      marque: ['', Validators.required],
      modele: ['', Validators.required],
      annee: ['', [Validators.required, Validators.min(1990), Validators.max(currentYear)]],
      couleur: ['#5A6E1A', Validators.required],
      description: [''],
      enMaintenance: [false]
    });
  }

  ngOnInit(): void {
    this.tracteurId = this.route.snapshot.params['id'];
    this.loadTracteur();
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
      puissanceMoteur: this.tracteurForm.value.puissanceMoteur,
      consommationCarburant: this.tracteurForm.value.consommationCarburant,
      kilometrage: this.tracteurForm.value.kilometrage,
      typeCarburant: this.tracteurForm.value.typeCarburant,
      remorqueAttachee: this.tracteurForm.value.remorqueAttachee,
      marque: this.tracteurForm.value.marque,
      modele: this.tracteurForm.value.modele,
      annee: this.tracteurForm.value.annee,
      couleur: this.tracteurForm.value.couleur,
      description: this.tracteurForm.value.description,
      statut: this.tracteurForm.value.enMaintenance ? 'MAINTENANCE' : 'DISPONIBLE'
    };

    this.tracteurService.update(this.tracteurId, tracteurData).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Tracteur modifié avec succès !';
        setTimeout(() => {
          this.router.navigate(['/ressources/tracteurs']);
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
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
