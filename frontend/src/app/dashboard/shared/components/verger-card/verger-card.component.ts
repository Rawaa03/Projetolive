import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-verger-card',
  templateUrl: './verger-card.component.html',
  styleUrls: ['./verger-card.component.css'],
})
export class VergerCardComponent {
  @Input() verger: any;

  getStatusColor(): string {
    if (!this.verger || !this.verger.statut) return '#95a5a6';
    const status = this.verger.statut.toUpperCase();
    switch (status) {
      case 'ACTIF':
        return '#27ae60';
      case 'INACTIF':
        return '#e74c3c';
      case 'EN_ATTENTE':
        return '#f39c12';
      default:
        return '#95a5a6';
    }
  }
}
