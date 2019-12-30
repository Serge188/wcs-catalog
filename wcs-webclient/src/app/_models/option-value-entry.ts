import {OfferOptionEntry} from "./offer-option-entry";
import {ImageEntry} from "./image-entry";

export class OptionValueEntry {
  id?: number;
  option?: OfferOptionEntry;
  value?: string;
  image?: ImageEntry;
  editMode?: boolean;
  imageInput?: any;
  selected?: boolean;
  orderNumber?: number;
}
