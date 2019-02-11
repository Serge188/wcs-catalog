import {OfferOptionEntry} from "./offer-option-entry";
import {ImageEntry} from "./image-entry";
import {OptionValueEntry} from "./option-value-entry";

export class SaleOfferEntry {
  id?: number;
  price?: number;
  mainImage?: ImageEntry;
  productId?: number;
  offerOption?: OfferOptionEntry;
  optionValueId?: number;
  optionValue?: OptionValueEntry;
  discountPrice?: number;
  buttonImage?: ImageEntry;
  imageInput?: any;
}
