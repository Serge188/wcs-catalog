import {ImageEntry} from "./image-entry";

export class PageEntry {
  id?: number;
  title?: string;
  alias?: string;
  sliderHeader?: string;
  sliderPromo?: string;
  sliderAnnotation?: string;
  content?: string;
  sliderImage?: ImageEntry;
  sliderImageInput?: any;
  image?: ImageEntry;
  imageInput?: any
  slider?: boolean;
  parentPageId?: number;
  parentPageTitle?: string;
  showInMainMenu?: boolean;
  showInSideMenu?: boolean;

  childPages: PageEntry[] = [];
  // level?: number;
  active?: boolean;
}
