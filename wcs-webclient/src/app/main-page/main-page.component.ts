import {Component, OnInit, ViewChild} from '@angular/core';
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {CatalogView} from "../_models/catalog-view";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
import {BrandService} from "../brand.service";
import {FactoryEntry} from "../_models/factory-entry";
import {SideMenuComponent} from "../side-menu/side-menu.component";

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
  public popularCategories: CategoryEntry[] = [];
  public popularCategoriesMap: Map<number, CategoryEntry[]> = new Map();
  public factories: FactoryEntry[];

  @ViewChild(SideMenuComponent)
  public sideMenu: SideMenuComponent;

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService,
              private brandService: BrandService) { }

  ngOnInit() {
    this.loadCategories();
    this.loadPopularProducts();
    this.loadPopularBrands();
  }

  public loadCategories(): void {
    this.categoriesService.getCategoriesWithProductsCount().subscribe(result => {
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
        if (cat.popular) {
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

  private loadPopularProducts(): void {
    this.productsService.getPopularProducts().subscribe(result => {
      this.products = result;
      for (let p of this.products) {
        p.link = "/catalog/mebel_dlya_kabineta/kresla_office/kreslo_van_gog/";
        this.calculateDiscount(p);
        if (p.saleOffers.length > 0) {
          this.setCurrentOffer(p, p.saleOffers[0]);
        }
      }
    });
  }

  private loadPopularBrands(): void {
    this.brandService.getPopularBrands().subscribe(result => {
      this.factories = result;
      for (let f of this.factories) {
        f.link = "/brands/" + f.alias;
      }
      console.log(this.factories);
    });
  }

  // public hasDiscount(product: ProductEntry): boolean {
  //   if (product.discountPrice) {
  //     return true;
  //   }
  //   return false;
  // }
  //
  // public changeView(view: string, event: any): void {
  //   let cardSwitcher: HTMLElement = document.getElementById("card-switcher");
  //   let gallerySwitcher: HTMLElement = document.getElementById("gallery-switcher");
  //   let listSwitcher: HTMLElement = document.getElementById("list-switcher");
  //   event.preventDefault();
  //   switch (view) {
  //     case "GALLERY":
  //       this.view = "GALLERY";
  //       cardSwitcher.classList.remove("selected");
  //       gallerySwitcher.classList.add("selected");
  //       listSwitcher.classList.remove("selected");
  //       break;
  //     case "CARD":
  //       this.view = "CARD";
  //       cardSwitcher.classList.add("selected");
  //       gallerySwitcher.classList.remove("selected");
  //       listSwitcher.classList.remove("selected");
  //       break;
  //     case "LIST":
  //       this.view = "CARD";
  //       cardSwitcher.classList.remove("selected");
  //       gallerySwitcher.classList.remove("selected");
  //       listSwitcher.classList.add("selected");
  //       break;
  //     default: this.view = "CARD";
  //   }
  // }

  // public getViewClass(view: string): string {
  //   if (view == this.view) {
  //     return "selected";
  //   } else {
  //     return "";
  //   }
  // }
  //
  // public productHasOptions(product: ProductEntry): boolean {
  //   return product.saleOffers.length > 0;
  // }
  //
  // public getCardImageLink(product: ProductEntry): string {
  //   if (product.offerCurrentImage) {
  //     return product.offerCurrentImage.cardImageLink;
  //   }
  //   return product.mainImage.cardImageLink;
  // }
  //
  public setCurrentOffer(p: ProductEntry, currentOffer: SaleOfferEntry) {
      if (currentOffer.buttonImage && currentOffer.buttonImage.optionImageLink) {
        p.optionsAreImages = true;
      }
      p.currentOffer = currentOffer;
      if (currentOffer.mainImage) {
        p.offerCurrentImage = currentOffer.mainImage;
      }
      p.price = currentOffer.price;
      p.discountPrice = currentOffer.discountPrice;
      this.calculateDiscount(p);
  }
  //
  // public getOptionsClass(p: ProductEntry): string {
  //   if (p.optionsAreImages) {
  //     return "is-pic";
  //   }
  //   return "";
  // }
  //
  // public getOfferClass(p: ProductEntry, offer: SaleOfferEntry): string {
  //   if (p.currentOffer.id == offer.id) {
  //     return "selected";
  //   }
  //   return "";
  // }
  //
  private calculateDiscount(p: ProductEntry): void {
    if (p.discountPrice) {
      let discount = (100 * (p.price - p.discountPrice)/p.price);
      if (discount < 1) {
        p.discount = (100 * (p.price - p.discountPrice)/p.price).toFixed(1);
      } else {
        p.discount = (100 * (p.price - p.discountPrice) / p.price).toFixed(0);
      }
    }
  }
  //
  // public getOptionButtonStyle(offer: SaleOfferEntry):any {
  //   if (offer.buttonImage && offer.buttonImage.optionImageLink) {
  //     let style = {'background-image': 'url(\'' + offer.buttonImage.optionImageLink + '\')'};
  //     return style;
  //   }
  //   return null;
  // }
}
