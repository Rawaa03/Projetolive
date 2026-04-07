import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { TracteurService } from '../../../services/tracteur.service';
import { Tracteur } from '../../../models/tracteur';

@Component({
  selector: 'app-modifier-tracteur',
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
  templateUrl: './modifier-tracteur.component.html',
  styleUrls: ['./modifier-tracteur.component.css']
})
export class ModifierTracteurComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  loadingData = true;
  error: string | null = null;
  success = false;
  tracteurId: string | null = null;
  tracteur: Tracteur | null = null;

  carburantOptions = [
    { value: 'DIESEL', label: 'Diesel' },
    { value: 'ESSENCE', label: 'Essence' },
    { value: 'ELECTRIQUE', label: 'Électrique' }
  ];

  statutOptions = [
    { value: 'DISPONIBLE', label: 'Disponible' },
    { value: 'EN_USE', label: 'En Utilisation' },
    { value: 'MAINTENANCE', label: 'Maintenance' },
    { value: 'HORS_SERVICE', label: 'Hors Service' }
  ];

  constructor(
    private fb: FormBuilder,
    private tracteurService: TracteurService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.tracteurId = params['id'];
      if (this.tracteurId) {
        this.loadTracteur();
      }
    });
    this.initializeForm();
  }

  initializeForm(): void {
    this.form = this.fb.group({
      marque: ['', Validators.required],
      modele: ['', Validators.required],
      annee: ['', [Validators.required, Validators.min(1950)]],
      puissanceMoteur: ['', [Validators.required, Validators.min(1)]],
      typeCarburant: ['', Validators.required],
      consommationCarburant: ['', [Validators.required, Validators.min(0)]],
      kilometrage: ['', [Validators.required, Validators.min(0)]],
      remorqueAttachee: [false],
      couleur: [''],
      description: [''],
      statut: ['', Validators.required]
    });
  }

  loadTracteur(): void {
    if (!this.tracteurId) return;

    this.tracteurService.getById(this.tracteurId).subscribe({
      next: (data) => {
        this.tracteur = data;
        this.form.patchValue({
          marque: data.marque,
          modele: data.modele,
          annee: data.annee,
          puissanceMoteur: data.puissanceMoteur,
          typeCarburant: data.typeCarburant,
          consommationCarburant: data.consommationCarburant,
          kilometrage: data.kilometrage,
          remorqueAttachee: data.remorqueAttachee,
          couleur: data.couleur,
          description: data.description,
          statut: data.statut
        });
        this.loadingData = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement du tracteur';
        console.error(err);
        this.loadingData = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.error = 'Veuillez remplir tous les champs requis correctement';
      return;
    }

    if (!this.tracteurId) {
      this.error = 'ID du tracteur non trouvé';
      return;
    }

    this.loading = true;
    const updates = this.form.value;

    this.tracteurService.update(this.tracteurId, updates).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/resources/tracteurs']);
        }, 1500);
      },
      error: (err) => {
        this.error = 'Erreur lors de la mise à jour du tracteur';
        console.error(err);
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/resources/tracteurs']);
  }
}
