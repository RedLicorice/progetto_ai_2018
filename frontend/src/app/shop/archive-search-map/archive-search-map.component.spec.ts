import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveSearchMapComponent } from './archive-search-map.component';

describe('ArchiveSearchMapComponent', () => {
  let component: ArchiveSearchMapComponent;
  let fixture: ComponentFixture<ArchiveSearchMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveSearchMapComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveSearchMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
