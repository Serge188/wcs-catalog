import {SaleOfferEntry} from "./sale-offer-entry";
import {ImageEntry} from "./image-entry";

export class ProductEntry {
  id?: number;
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
  saleOffers?: SaleOfferEntry[] = [];
  currentOffer?: SaleOfferEntry;
  optionsAreImages?: boolean;
  offerCurrentImage?: ImageEntry;
}
