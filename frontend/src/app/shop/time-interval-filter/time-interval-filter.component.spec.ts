import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeIntervalFilterComponent } from './time-interval-filter.component';

describe('TimeIntervalFilterComponent', () => {
  let component: TimeIntervalFilterComponent;
  let fixture: ComponentFixture<TimeIntervalFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimeIntervalFilterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeIntervalFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
