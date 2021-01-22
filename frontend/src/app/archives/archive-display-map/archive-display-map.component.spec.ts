import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveDisplayMapComponent } from './archive-display-map.component';

describe('ArchiveDisplayMapComponent', () => {
  let component: ArchiveDisplayMapComponent;
  let fixture: ComponentFixture<ArchiveDisplayMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveDisplayMapComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveDisplayMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
