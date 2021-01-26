import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveEditorComponent } from './archive-editor.component';

describe('ArchiveEditorComponent', () => {
  let component: ArchiveEditorComponent;
  let fixture: ComponentFixture<ArchiveEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveEditorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
