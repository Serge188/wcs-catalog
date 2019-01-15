import {ImageEntry} from "./image-entry";

export class CategoryEntry {
  id?: number;
  title?: string;
  link?: string;
  parentCategoryId?: number;
  childCategories?: CategoryEntry[] = [];
  image?: ImageEntry;
  popular?: boolean;
  hiddenSubCategoriesQty?: number;
  hasHiddenSubCategories?: boolean;
  alias?: string;
}
