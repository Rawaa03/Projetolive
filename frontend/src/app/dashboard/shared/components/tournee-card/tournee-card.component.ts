import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-tournee-card',
  templateUrl: './tournee-card.component.html',
  styleUrls: ['./tournee-card.component.css'],
})
export class TourneeCardComponent {
  @Input() tournee: any;

  getStatusColor(): string {
    if (!this.tournee || !this.tournee.statut) return '#95a5a6';
    const status = this.tournee.statut.toUpperCase();
    switch (status) {
      case 'EN_COURS':
        return '#f39c12';
      case 'TERMINEE':
        return '#27ae60';
      case 'PLANIFIEE':
        return '#3498db';
      case 'ANNULEE':
        return '#e74c3c';
      default:
        return '#95a5a6';
    }
  }

  getStatusLabel(): string {
    if (!this.tournee || !this.tournee.statut) return 'Unknown';
    return this.tournee.statut.replace('_', ' ');
  }
}
