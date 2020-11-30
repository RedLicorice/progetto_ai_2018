import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveMapComponent } from './archive-map.component';

describe('ArchiveMapComponent', () => {
  let component: ArchiveMapComponent;
  let fixture: ComponentFixture<ArchiveMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveMapComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
