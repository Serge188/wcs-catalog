import { Component, OnInit } from '@angular/core';
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent implements OnInit {

  public categories: CategoryEntry[];

  constructor(private categoriesService: CategoriesService) {}

  ngOnInit() {
    this.categoriesService.getTopLevelCategories().subscribe(result => {
      this.categories = result;
    });
  }

}
