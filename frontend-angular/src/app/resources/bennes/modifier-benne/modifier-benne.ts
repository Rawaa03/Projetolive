import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';

import { BenneService } from '../../../services/benne.service';
import { Benne } from '../../../models/benne';

@Component({
  selector: 'app-modifier-benne',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule
  ],
  templateUrl: './modifier-benne.component.html',
  styleUrls: ['./modifier-benne.component.css']
})
export class ModifierBenneComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  loadingData = true;
  error: string | null = null;
  success = false;
  benneId: string | null = null;
  benne: Benne | null = null;

  materiauxOptions = [
    { value: 'PLASTIQUE', label: 'Plastique' },
    { value: 'METAL', label: 'Métal' },
    { value: 'BOIS', label: 'Bois' }
  ];

  statutOptions = [
    { value: 'DISPONIBLE', label: 'Disponible' },
    { value: 'EN_USE', label: 'En Utilisation' },
    { value: 'MAINTENANCE', label: 'Maintenance' },
    { value: 'HORS_SERVICE', label: 'Hors Service' }
  ];

  constructor(
    private fb: FormBuilder,
    private benneService: BenneService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.benneId = params['id'];
      if (this.benneId) {
        this.loadBenne();
      }
    });
    this.initializeForm();
  }

  initializeForm(): void {
    this.form = this.fb.group({
      capaciteMax: ['', [Validators.required, Validators.min(100)]],
      typeMateriau: ['', Validators.required],
      usure: ['0', [Validators.required, Validators.min(0), Validators.max(100)]],
      couleur: [''],
      description: [''],
      statut: ['', Validators.required]
    });
  }

  loadBenne(): void {
    if (!this.benneId) return;

    this.benneService.getById(this.benneId).subscribe({
      next: (data) => {
        this.benne = data;
        this.form.patchValue({
          capaciteMax: data.capaciteMax,
          typeMateriau: data.typeMateriau,
          usure: data.usure,
          couleur: data.couleur,
          description: data.description,
          statut: data.statut
        });
        this.loadingData = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de la benne';
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

    if (!this.benneId) {
      this.error = 'ID de la benne non trouvé';
      return;
    }

    this.loading = true;
    const updates = this.form.value;

    this.benneService.update(this.benneId, updates).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/resources/bennes']);
        }, 1500);
      },
      error: (err) => {
        this.error = 'Erreur lors de la mise à jour de la benne';
        console.error(err);
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/resources/bennes']);
  }
}
