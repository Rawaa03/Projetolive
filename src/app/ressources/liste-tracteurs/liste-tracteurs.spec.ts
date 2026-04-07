import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListeTracteurs } from './liste-tracteurs';

describe('ListeTracteurs', () => {
  let component: ListeTracteurs;
  let fixture: ComponentFixture<ListeTracteurs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListeTracteurs],
    }).compileComponents();

    fixture = TestBed.createComponent(ListeTracteurs);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
