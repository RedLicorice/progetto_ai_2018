import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveUploadComponent } from './archive-upload.component';

describe('ArchiveUploadComponent', () => {
  let component: ArchiveUploadComponent;
  let fixture: ComponentFixture<ArchiveUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveUploadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
