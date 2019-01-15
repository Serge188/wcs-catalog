import {OfferOptionEntry} from "./offer-option-entry";
import {ImageEntry} from "./image-entry";

export class SaleOfferEntry {
  id?: number;
  price?: number;
  mainImage?: ImageEntry;
  productId?: number;
  offerOption?: OfferOptionEntry;
  optionValue?: string;
  discountPrice?: number;
  buttonImage?: ImageEntry;
}
