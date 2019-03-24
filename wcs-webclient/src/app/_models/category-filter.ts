import {OfferOptionEntry} from "./offer-option-entry";

export class CategoryFilter {
  minPrice?: number;
  maxPrice?: number;
  factoryIds?: number[];
  options?: OfferOptionEntry[];
}
