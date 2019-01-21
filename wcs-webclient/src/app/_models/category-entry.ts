import {ImageEntry} from "./image-entry";

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
}
