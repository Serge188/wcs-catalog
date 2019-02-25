import { TestBed } from '@angular/core/testing';

import { FactoriesService } from './factories.service';

describe('FactoriesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FactoriesService = TestBed.get(FactoriesService);
    expect(service).toBeTruthy();
  });
});
