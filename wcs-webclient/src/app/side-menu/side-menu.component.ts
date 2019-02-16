import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CategoryEntry} from "../_models/category-entry";
import {CategoriesService} from "../categories.service";

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.css']
})
export class SideMenuComponent implements OnInit {

  @Input() hideMenu: boolean;
  public categories: CategoryEntry[] = [];

  constructor(private categoriesService: CategoriesService) { }

  ngOnInit() {
    this.loadCategories();
  }

  public getMenuClass(): string {
    if (!this.hideMenu) {
      return "rs-show";
    }
    return "";
  }

  private loadCategories(): void {
    this.categoriesService.getCategories().subscribe(result => {
      for (let cat of result) {
        if (!cat.parentCategoryId) {
          cat.link = "/catalog/" + cat.alias;
          cat.childCategories = [];
          this.categories.push(cat);
        }
      }
      for (let cat of result) {
        if (cat.parentCategoryId) {
          let parentCat = this.categories.find(x => x.id == cat.parentCategoryId);
          if (parentCat) {
            cat.link = parentCat.link + "/" + cat.alias;
            parentCat.childCategories.push(cat);
          }
        }
      }
    });
  }
}
