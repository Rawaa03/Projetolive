import { Component, Input, OnInit } from '@angular/core';
import { ChartOptions, ChartType } from 'chart.js';

@Component({
  selector: 'app-stat-chart',
  templateUrl: './stat-chart.component.html',
  styleUrls: ['./stat-chart.component.css'],
})
export class StatChartComponent implements OnInit {
  @Input() chartType: ChartType = 'pie';
  @Input() chartData: any;
  @Input() chartOptions: ChartOptions = {};
  @Input() chartLabels: string[] = [];

  ngOnInit(): void {
    if (!this.chartOptions) {
      this.chartOptions = {
        responsive: true,
        maintainAspectRatio: true,
      };
    }
  }
}
