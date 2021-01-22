import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurchasedArchivesTableComponent } from './purchased-archives-table.component';

describe('PurchasedArchivesTableComponent', () => {
  let component: PurchasedArchivesTableComponent;
  let fixture: ComponentFixture<PurchasedArchivesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PurchasedArchivesTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchasedArchivesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
