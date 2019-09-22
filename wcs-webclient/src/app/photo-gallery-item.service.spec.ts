import { TestBed } from '@angular/core/testing';

import { PhotoGalleryItemService } from './photo-gallery-item.service';

describe('PhotoGalleryItemService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PhotoGalleryItemService = TestBed.get(PhotoGalleryItemService);
    expect(service).toBeTruthy();
  });
});
