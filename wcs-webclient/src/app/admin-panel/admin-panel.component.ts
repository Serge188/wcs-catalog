import { Component, OnInit } from '@angular/core';
import {CategoriesService} from "../categories.service";
import {AppComponent} from "../app.component";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {Observable} from "rxjs/internal/Observable";

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  public categories: CategoryEntry[];
  public topLevelCategories: CategoryEntry[] = [];
  public itemsInList: any[] = [];
  public newCategory: CategoryEntry = {};
  public view: any;

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService) { }

  ngOnInit() {
    this.view = "categories";
    this.loadCategories();
  }

  public loadCategories(): void {
    this.itemsInList = [];
    this.topLevelCategories = [];
    this.categoriesService.getCategories().subscribe(result => {
      console.log(result);
      this.categories = result;
      for (let cat of this.categories) {
        cat.expanded = false;
        cat.selected = false;
        if (!cat.parentCategoryId) {
          cat.level = 1;
          this.itemsInList.push(cat);
          this.topLevelCategories.push(cat);
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

  public openModalCategory(event: any, item: any, createChild: boolean): void {
    event.preventDefault();
    let mask: HTMLElement = document.getElementById("cover-mask");
    let form: HTMLElement = document.getElementById("modal-category");
    mask.classList.add("modal-visible");
    mask.classList.remove("modal-hidden");
    form.classList.add("modal-visible");
    form.classList.remove("modal-hidden");
    if (item) {
      if (createChild) {
        this.newCategory.parentCategoryId = item.id;
      } else {
        this.newCategory.id = item.id;
        this.newCategory.title = item.title;
        this.newCategory.parentCategoryId = item.parentCategoryId;
        this.newCategory.description = item.description;
        this.newCategory.popular = item.popular;
        this.newCategory.image = item.image;
      }
    }
  }

  private openModal(modalId: string): void {

  }

  public closeModal(modalId: string) {
    let mask: HTMLElement = document.getElementById("cover-mask");
    let form: HTMLElement = document.getElementById(modalId);
    mask.classList.remove("modal-visible");
    mask.classList.add("modal-hidden");
    form.classList.remove("modal-visible");
    form.classList.add("modal-hidden");
    this.newCategory = {};
  }


  public createOrUpdateCategory(): void {
    let result: Observable<any>;
    if (this.newCategory.id) {
      result = this.categoriesService.updateCategory(this.newCategory);
    } else {
      result = this.categoriesService.createCategory(this.newCategory);
    }
    result.subscribe(() => {
      console.log("hjagf");
      this.loadCategories();
    });
    this.newCategory = {};
    this.closeModal("modal-category");
  }

  public addFile() {
    let file  = (<HTMLInputElement>document.getElementById("imageUploader")).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      if (this.newCategory.image) {
        this.newCategory.image.categoryImageLink = null;
      }
      this.newCategory.imageInput = reader.result;
      this.newCategory.imageChanged = true;
    });
  }

  public removeCategory(categoryId: number): void {
    this.categoriesService.removeCategory(categoryId).subscribe(
      res => {
      this.closeModal("modal-category");
      let cat = this.itemsInList.find(x => x.id == categoryId);
      if (cat) {
        let index = this.itemsInList.indexOf(cat, 0);
        this.itemsInList.splice(index, 1);
      }
    },
        err => {
        alert("При удалении категории произошла оишбка: " + err);
      });
  }

  public changeView(viewName: string, event: any): void {
    event.preventDefault();
    this.view = viewName;
  }

}
