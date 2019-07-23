import {ImageEntry} from "./image-entry";

export class PhotoGalleryItemEntry {
  id?: number;
  title?: string;
  alias?: string;
  mainImage?: ImageEntry;
  images?: ImageEntry[];
}
