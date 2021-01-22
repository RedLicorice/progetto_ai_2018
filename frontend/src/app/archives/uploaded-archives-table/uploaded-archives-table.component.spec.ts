import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadedArchivesTableComponent } from './uploaded-archives-table.component';

describe('UploadedArchivesTableComponent', () => {
  let component: UploadedArchivesTableComponent;
  let fixture: ComponentFixture<UploadedArchivesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadedArchivesTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadedArchivesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
