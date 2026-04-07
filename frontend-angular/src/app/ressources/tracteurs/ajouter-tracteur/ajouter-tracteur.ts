import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TracteurService } from '../../../services/tracteur.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TracteurCreation } from '../../../models/tracteur.model';

@Component({
  selector: 'app-ajouter-tracteur',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './ajouter-tracteur.html',
  styleUrls: ['./ajouter-tracteur.css']
})
export class AjouterTracteurComponent implements OnInit {
  tracteurForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  carburants: string[] = ['DIESEL', 'ESSENCE', 'ELECTRIQUE', 'HYBRIDE'];

  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
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
      description: ['']
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.tracteurForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const tracteurData: TracteurCreation = {
      puissanceMoteur: this.tracteurForm.value.puissanceMoteur,
      consommationCarburant: this.tracteurForm.value.consommationCarburant,
      kilometrage: this.tracteurForm.value.kilometrage,
      typeCarburant: this.tracteurForm.value.typeCarburant,
      remorqueAttachee: this.tracteurForm.value.remorqueAttachee,
      marque: this.tracteurForm.value.marque,
      modele: this.tracteurForm.value.modele,
      annee: this.tracteurForm.value.annee,
      couleur: this.tracteurForm.value.couleur,
      description: this.tracteurForm.value.description
    };

    this.tracteurService.create(tracteurData).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Tracteur ajouté avec succès !';
        setTimeout(() => {
          this.router.navigate(['/ressources/tracteurs']);
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Erreur lors de l\'ajout du tracteur';
        console.error('Erreur:', err);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/ressources/tracteurs']);
  }
}
