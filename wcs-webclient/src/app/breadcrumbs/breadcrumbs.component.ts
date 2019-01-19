import {Component, Input, OnInit} from '@angular/core';
import {CategoryEntry} from "../_models/category-entry";

@Component({
  selector: 'app-breadcrumbs',
  templateUrl: './breadcrumbs.component.html',
  styleUrls: ['./breadcrumbs.component.css']
})
export class BreadcrumbsComponent implements OnInit {

  @Input() currentTitle;
  @Input() category: CategoryEntry;
  @Input() isProduct: boolean;
  @Input() isCategory: boolean;

  constructor() { }

  ngOnInit() {
  }

  public hasCatalogLink(): boolean {
    return this.isCategory || this.isProduct;
  }

  public hasParentCategory(): boolean {
    return this.category != null && this.category.parentCategoryId != null;
  }

  public getParentCategoryLink(): string {
    if (this.category && this.category.parentCategoryAlias) {
      return "/catalog/" + this.category.parentCategoryAlias;
    }
    return "/catalog";
  }

  public getCategoryLink(): string {
    return this.getParentCategoryLink() + "/" + this.category.alias + "/";
  }

}
