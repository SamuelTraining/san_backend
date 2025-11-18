import { TestBed } from '@angular/core/testing';
import { TrdVersionService } from './trd-version/trd-version.service';

describe('TrdVersionService', () => {
  let service: TrdVersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrdVersionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
