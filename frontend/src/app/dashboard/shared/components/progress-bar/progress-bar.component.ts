import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-progress-bar',
  templateUrl: './progress-bar.component.html',
  styleUrls: ['./progress-bar.component.css'],
})
export class ProgressBarComponent {
  @Input() label: string = '';
  @Input() percentage: number = 0;
  @Input() color: string = '#3498db';
  @Input() height: string = '8px';
  @Input() showPercentage: boolean = true;

  constructor() { }
}
