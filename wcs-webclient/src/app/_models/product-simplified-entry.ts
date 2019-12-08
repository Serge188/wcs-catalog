import {CategoryEntry} from "./category-entry";
import {ImageEntry} from "./image-entry";
import {OfferOptionEntry} from "./offer-option-entry";
import {SaleOfferEntry} from "./sale-offer-entry";
import {FactoryEntry} from "./factory-entry";
import {PriceType} from "./product-entry";

export class ProductSimplifiedEntry {
  id?; number;
  title?: string;
  alias?: string;
  imageLink?: string;
  price?: number;
  link?: string;
  selected?: boolean;
}
