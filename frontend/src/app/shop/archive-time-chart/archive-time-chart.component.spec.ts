import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveTimeChartComponent } from './archive-time-chart.component';

describe('TimeChartComponent', () => {
  let component: ArchiveTimeChartComponent;
  let fixture: ComponentFixture<ArchiveTimeChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveTimeChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveTimeChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
