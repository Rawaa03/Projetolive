import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-alert-badge',
  templateUrl: './alert-badge.component.html',
  styleUrls: ['./alert-badge.component.css'],
})
export class AlertBadgeComponent {
  @Input() alert: any;

  getAlertColor(): string {
    if (!this.alert || !this.alert.level) return '#95a5a6';
    const level = this.alert.level.toUpperCase();
    switch (level) {
      case 'CRITICAL':
        return '#e74c3c';
      case 'WARNING':
        return '#f39c12';
      case 'INFO':
        return '#3498db';
      default:
        return '#95a5a6';
    }
  }

  getAlertIcon(): string {
    if (!this.alert || !this.alert.level) return '•';
    const level = this.alert.level.toUpperCase();
    switch (level) {
      case 'CRITICAL':
        return '⚠';
      case 'WARNING':
        return '⚡';
      case 'INFO':
        return 'ℹ';
      default:
        return '•';
    }
  }
}
