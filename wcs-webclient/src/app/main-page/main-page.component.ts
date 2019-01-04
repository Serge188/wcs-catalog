import { Component, OnInit } from '@angular/core';
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  customOptions: any = {
    // loop: true,
    // mouseDrag: false,
    // touchDrag: false,
    // pullDrag: false,
    // dots: false,
    // navSpeed: 700,
    // navText: ['', ''],
    // nav: true
  };

  public categories: CategoryEntry[] = [];

  constructor(private categoriesService: CategoriesService) { }

  ngOnInit() {
    this.loadCategories();
  }

  private loadCategories(): void {
    this.categoriesService.getCategories().subscribe(result => {
      for (let cat of result) {
        if (!cat.parentCategoryId) {
          cat.childCategories = [];
          this.categories.push(cat);
        }
      }
      for (let cat of result) {
        if (cat.parentCategoryId) {
          let parentCat = this.categories.find(x => x.id == cat.parentCategoryId);
          if (parentCat) {
            parentCat.childCategories.push(cat);
          }
        }
      }
    });
  }

}
