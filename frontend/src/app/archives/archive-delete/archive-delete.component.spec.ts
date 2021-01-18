import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveDeleteComponent } from './archive-delete.component';

describe('ArchiveDeleteComponent', () => {
  let component: ArchiveDeleteComponent;
  let fixture: ComponentFixture<ArchiveDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveDeleteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
