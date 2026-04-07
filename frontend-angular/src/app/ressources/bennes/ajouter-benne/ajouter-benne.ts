import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BenneService } from '../../../services/benne.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { BenneCreation } from '../../../models/benne.model';

@Component({
  selector: 'app-ajouter-benne',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './ajouter-benne.html',
  styleUrls: ['./ajouter-benne.css']
})
export class AjouterBenneComponent implements OnInit {
  benneForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  materiaux: string[] = ['OLIVES', 'PLASTIQUE', 'METAL', 'BOIS', 'GENERAL'];

  constructor(
    private fb: FormBuilder,
    private benneService: BenneService,
    private router: Router
  ) {
    this.benneForm = this.fb.group({
      capaciteMax: ['', [Validators.required, Validators.min(100)]],
      typeMateriau: ['', Validators.required],
      usure: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      couleur: ['#5A6E1A', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.benneForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const benneData: BenneCreation = {
      capaciteMax: this.benneForm.value.capaciteMax,
      typeMateriau: this.benneForm.value.typeMateriau,
      usure: this.benneForm.value.usure,
      couleur: this.benneForm.value.couleur,
      description: this.benneForm.value.description
    };

    this.benneService.create(benneData).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Benne ajoutée avec succès !';
        setTimeout(() => {
          this.router.navigate(['/ressources/bennes']);
        }, 1500);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Erreur lors de l\'ajout de la benne';
        console.error('Erreur:', err);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/ressources/bennes']);
  }
}
