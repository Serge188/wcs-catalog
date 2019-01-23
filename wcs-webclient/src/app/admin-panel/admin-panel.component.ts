import { Component, OnInit } from '@angular/core';
import {CategoriesService} from "../categories.service";
import {AppComponent} from "../app.component";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  public categories: CategoryEntry[];
  public itemsInList: any[] = [];

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService) { }

  ngOnInit() {
    this.loadCategories();
  }

  public loadCategories(): void {
    this.categoriesService.getCategories().subscribe(result => {
      this.categories = result;
      for (let cat of this.categories) {
        if (!cat.parentCategoryId) {
          cat.level = 1;
          cat.expanded = false;
          this.itemsInList.push(cat);
        }
      }
    });
  }

  public loadProductsForCategory(cat: CategoryEntry): void {
      this.productsService.getOneLevelCategoryProducts(cat.id).subscribe(result => {
        cat.products = result;
        let index = this.itemsInList.indexOf(cat, 0);
        if (index != -1) {
          for (let p of cat.products) {
            index++;
            this.itemsInList.splice(index, 0, p);
            p.level = cat.level + 1;
          }
        }
        cat.expanded = true;
      });
  }

  public removeProductsFromList(category: CategoryEntry): void {
    console.log(category);
    for (let p of category.products) {
      console.log(p);
      let index = this.itemsInList.indexOf(p, 0);
      if (index != -1) {
        this.itemsInList.splice(index, 1);
      }
    }
  }

  public getActiveClass(category: CategoryEntry) {
    if (category.selected) {
      return "active";
    }
    return "";
  }

  public toggleCategory(category: CategoryEntry) {
    if (!category.selected) {
      for (let cat of this.categories) {
        cat.selected = false;
      }
      category.selected = true;
    }
    if (!category.expanded) {
      this.expandCategory(category);
    } else {
      this.collapseCategory(category);
    }
  }

  private expandCategory(category: CategoryEntry): void {
    category.expanded = true;
    for (let cat of this.categories) {
      if (cat.parentCategoryId == category.id) {
        let index = this.itemsInList.indexOf(category, 0);
        if (index != -1) {
          this.itemsInList.splice(index + 1, 0, cat);
          cat.level = category.level + 1;
        }
      }
    }
    this.loadProductsForCategory(category);
  }

  private collapseCategory(category: CategoryEntry):void {
    for (let cat of this.categories) {
      if (cat.parentCategoryId == category.id) {
        if (cat.products && cat.products.length > 0) {
          this.removeProductsFromList(cat);
        }
        cat.expanded = false;
        let index = this.itemsInList.indexOf(cat, 0);
        if (index != -1) {
          this.itemsInList.splice(index, 1);
        }
      }
    }
    this.removeProductsFromList(category);
    category.expanded = false;
  }

  public getPadding(category: CategoryEntry): any {
      let padding: any = {};
      if (category.level == 1) {
        padding['font-weight'] = 'bold';
      }
      if (category.level > 1) {
        let value = (category.level - 1) * 45;
        padding['padding-left'] = value + 'px';
      }
      return padding;
  }

}
