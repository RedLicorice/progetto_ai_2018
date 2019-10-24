import { TestBed, inject } from '@angular/core/testing';
import { PositionsService } from './positions.service';

describe('PositionsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PositionsService]
    });
  });

  it('should be created', inject([PositionsService], (service: PositionsService) => {
    expect(service).toBeTruthy();
  }));
});
