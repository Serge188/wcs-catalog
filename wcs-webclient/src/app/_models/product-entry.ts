import {SaleOfferEntry} from "./sale-offer-entry";
import {ImageEntry} from "./image-entry";
import {CategoryEntry} from "./category-entry";
import {OfferOptionEntry} from "./offer-option-entry";
import {FactoryEntry} from "./factory-entry";

export enum PriceType {NORMAL = "NORMAL", PRICE_FROM = "PRICE_FROM", UNIT_PRICE_FROM = "UNIT_PRICE_FROM", FROM_TO = "FROM_TO", PRICE_FOR_SET = "PRICE_FOR_SET"}

export class ProductEntry {
  id?: number;
  categoryId?: number;
  category?: CategoryEntry;
  title?: string;
  mainImage?: ImageEntry;
  images?: ImageEntry[];
  alias?: string;
  description?: string;
  shortDescription?: string;
  productOfDay?: boolean;
  newProduct?: boolean;
  hit?: boolean;
  promo?: boolean;
  popular?: boolean;
  price?: number;
  discountPrice?: number;
  link?: string;
  altTitle?: string;
  discount?: string;
  options?: OfferOptionEntry[] = [];
  saleOffers?: SaleOfferEntry[] = [];
  currentOffer?: SaleOfferEntry;
  optionsAreImages?: boolean;
  offerCurrentImage?: ImageEntry;
  level?: number;
  expanded?: boolean;
  imageInput?: any;
  imagesInput?: any[];
  priceType?: PriceType;
  factory?: FactoryEntry;
  factoryId?: number;
  favorite?: boolean;
  compared?: boolean;
}
