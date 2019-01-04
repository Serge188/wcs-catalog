export class CategoryEntry {
  id?: number;
  title?: string;
  link?: string;
  parentCategoryId?: number;
  childCategories?: CategoryEntry[] = [];
}
