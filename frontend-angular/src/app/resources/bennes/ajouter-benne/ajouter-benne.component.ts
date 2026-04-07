import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';

import { BenneService } from '../../../services/benne.service';
import { BenneCreation } from '../../../models/benne';

@Component({
  selector: 'app-ajouter-benne',
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
  templateUrl: './ajouter-benne.component.html',
  styleUrls: ['./ajouter-benne.component.css']
})
export class AjouterBenneComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  error: string | null = null;
  success = false;

  materiauxOptions = [
    { value: 'PLASTIQUE', label: 'Plastique' },
    { value: 'METAL', label: 'Métal' },
    { value: 'BOIS', label: 'Bois' }
  ];

  constructor(
    private fb: FormBuilder,
    private benneService: BenneService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.form = this.fb.group({
      capaciteMax: ['', [Validators.required, Validators.min(100)]],
      typeMateriau: ['', Validators.required],
      usure: ['0', [Validators.required, Validators.min(0), Validators.max(100)]],
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
    const benneData: BenneCreation = this.form.value;

    this.benneService.create(benneData).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/resources/bennes']);
        }, 1500);
      },
      error: (err) => {
        this.error = 'Erreur lors de la création de la benne';
        console.error(err);
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/resources/bennes']);
  }
}
