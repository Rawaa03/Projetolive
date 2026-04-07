import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { TracteurService } from '../../../services/tracteur.service';
import { TracteurCreation } from '../../../models/tracteur';

@Component({
  selector: 'app-ajouter-tracteur',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule,
    MatCheckboxModule
  ],
  templateUrl: './ajouter-tracteur.component.html',
  styleUrls: ['./ajouter-tracteur.component.css']
})
export class AjouterTracteurComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;

  carburantOptions = [
    { value: 'DIESEL', label: 'Diesel' },
    { value: 'ESSENCE', label: 'Essence' },
    { value: 'ELECTRIQUE', label: 'Électrique' }
  ];

  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.form = this.fb.group({
      marque: ['', Validators.required],
      modele: ['', Validators.required],
      annee: ['', [Validators.required, Validators.min(1950), Validators.max(new Date().getFullYear() + 1)]],
      puissanceMoteur: ['', [Validators.required, Validators.min(1)]],
      typeCarburant: ['', Validators.required],
      consommationCarburant: ['', [Validators.required, Validators.min(0)]],
      kilometrage: ['0', [Validators.required, Validators.min(0)]],
      remorqueAttachee: [false],
      couleur: [''],
      description: ['']
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.error = 'Veuillez remplir tous les champs requis correctement';
      return;
    }

    this.loading = true;
    const tracteurData: TracteurCreation = this.form.value;

    this.tracteurService.create(tracteurData).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/resources/tracteurs']);
        }, 1500);
      },
      error: (err) => {
        this.error = 'Erreur lors de la création du tracteur';
        console.error(err);
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/resources/tracteurs']);
  }
}
