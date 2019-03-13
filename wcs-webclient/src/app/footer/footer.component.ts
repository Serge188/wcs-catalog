import { Component, OnInit } from '@angular/core';
import {CategoryEntry} from "../_models/category-entry";
import {CategoriesService} from "../categories.service";
import {PageService} from "../page.service";
import {PageEntry} from "../_models/page-entry";

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  public categories: CategoryEntry[] = [];

  public popularCategories: CategoryEntry[] = [];
  public popularCategoriesMap: Map<number, CategoryEntry[]> = new Map();

  public mainMenuPages: PageEntry[];

  constructor(private categoriesService: CategoriesService, private pageService: PageService) { }

  ngOnInit() {
    this.loadCategories();
    this.pageService.getMainMenuPages().subscribe(result => this.mainMenuPages = result);
  }

  public loadCategories(): void {
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

      for (let cat of this.categories) {
        if (cat.popular && this.popularCategoriesMap.size < 6) {
          this.popularCategories.push(cat);
          let shortChildList: CategoryEntry[] = [];
          if (cat.childCategories.length > 5) {
            for (let i = 0; i < 5; i++) {
              shortChildList[i] = cat.childCategories[i];
            }
            cat.hiddenSubCategoriesQty = cat.childCategories.length - shortChildList.length;
            cat.hasHiddenSubCategories = true;
            this.popularCategoriesMap.set(cat.id, shortChildList);
          } else {
            cat.hiddenSubCategoriesQty = 0;
            cat.hasHiddenSubCategories = false;
            this.popularCategoriesMap.set(cat.id, cat.childCategories);
          }
        }
      }
    });
  };

}
