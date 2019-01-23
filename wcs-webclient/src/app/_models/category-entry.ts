import {ImageEntry} from "./image-entry";
import {ProductEntry} from "./product-entry";

export class CategoryEntry {
  id?: number;
  title?: string;
  link?: string;
  parentCategoryId?: number;
  parentCategoryTitle?: string;
  parentCategoryAlias?: string;
  childCategories?: CategoryEntry[] = [];
  image?: ImageEntry;
  popular?: boolean;
  hiddenSubCategoriesQty?: number;
  hasHiddenSubCategories?: boolean;
  alias?: string;
  description?: string;
  selected?: boolean = false;
  level?: number;
  expanded?: boolean;
  products?: ProductEntry[] = [];
}
