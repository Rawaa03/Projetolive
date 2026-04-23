import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.css'],
})
export class DataTableComponent {
  @Input() columns: any[] = [];
  @Input() data: any[] = [];
  @Input() title: string = '';

  constructor() { }
}
