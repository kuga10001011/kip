import { TestBed } from '@angular/core/testing';

import { RowService } from './row.service';

describe('JobListingService', () => {
  let service: RowService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RowService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
