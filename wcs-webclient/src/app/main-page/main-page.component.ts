import { Component, OnInit } from '@angular/core';
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {CatalogView} from "../_models/catalog-view";

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
  public products: ProductEntry[];
  public view: any = "CARD";

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService) { }

  ngOnInit() {
    this.loadCategories();
    this.loadPopularProducts();
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

  private loadPopularProducts(): void {
    this.productsService.getPopularProducts().subscribe(result => {
      this.products = result;
      for (let p of this.products) {
        p.link = "/catalog/mebel_dlya_kabineta/kresla_office/kreslo_van_gog/";
        if (p.discountPrice) {
          let discount = (100 * (p.price - p.discountPrice)/p.price);
          if (discount < 1) {
            p.discount = (100 * (p.price - p.discountPrice)/p.price).toFixed(1);
          } else {
            p.discount = (100 * (p.price - p.discountPrice) / p.price).toFixed(0);
          }
        }

        if (this.view === 'CARD') {
          p.mainImage = "/assets/img/catalog/198_132/" + p.alias + ".jpg";
        } else if (this.view === "GALLERY") {
          p.mainImage = "/assets/img/catalog/100_100/" + p.alias + ".jpg";
        }
      }
      console.log(this.products);
    });
  }

  public hasDiscount(product: ProductEntry): boolean {
    if (product.discountPrice) {
      return true;
    }
    return false;
  }

  public changeView(view: string, event: any): void {
    let cardSwitcher: HTMLElement = document.getElementById("card-switcher");
    let gallerySwitcher: HTMLElement = document.getElementById("gallery-switcher");
    let listSwitcher: HTMLElement = document.getElementById("list-switcher");
    event.preventDefault();
    switch (view) {
      case "GALLERY":
        this.view = "GALLERY";
        cardSwitcher.classList.remove("selected");
        gallerySwitcher.classList.add("selected");
        listSwitcher.classList.remove("selected");
        break;
      case "CARD":
        this.view = "CARD";
        cardSwitcher.classList.add("selected");
        gallerySwitcher.classList.remove("selected");
        listSwitcher.classList.remove("selected");
        break;
      case "LIST":
        this.view = "CARD";
        cardSwitcher.classList.remove("selected");
        gallerySwitcher.classList.remove("selected");
        listSwitcher.classList.add("selected");
        break;
      default: this.view = "CARD";
    }
  }

  public getViewClass(view: string): string {
    if (view == this.view) {
      return "selected";
    } else {
      return "";
    }
  }
}
