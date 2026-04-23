import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-dashboard-card',
  templateUrl: './dashboard-card.component.html',
  styleUrls: ['./dashboard-card.component.css'],
})
export class DashboardCardComponent {
  @Input() title: string = '';
  @Input() value: string | number = '';
  @Input() unit: string = '';
  @Input() icon: string = '📊';
  @Input() trend: number | null = null;
  @Input() backgroundColor: string = '#ffffff';
  @Input() borderColor: string = '#e0e0e0';

  getTrendClass(): string {
    if (this.trend === null) return '';
    return this.trend >= 0 ? 'trend-up' : 'trend-down';
  }

  getTrendIcon(): string {
    if (this.trend === null) return '';
    return this.trend >= 0 ? '▲' : '▼';
  }
}
