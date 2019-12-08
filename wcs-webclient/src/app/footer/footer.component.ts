import { Component, OnInit } from '@angular/core';
import {CategoryEntry} from "../_models/category-entry";
import {CategoriesService} from "../categories.service";
import {PageService} from "../page.service";
import {PageEntry} from "../_models/page-entry";
import {ProductSimplifiedEntry} from "../_models/product-simplified-entry";
import {ProductsService} from "../products.service";
declare var jQuery: any;

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  public categories: CategoryEntry[] = [];

  public popularCategories: CategoryEntry[] = [];
  public popularCategoriesMap: Map<number, CategoryEntry[]> = new Map();

  public mainMenuPages: PageEntry[] = [];
  public bottomPanelType: string = null;
  public viewedProducts: ProductSimplifiedEntry[] = [];
  public favoriteProducts: ProductSimplifiedEntry[] = [];

  constructor(
    private categoriesService: CategoriesService,
    private pageService: PageService,
    private productService: ProductsService) { }

  ngOnInit() {
    this.loadCategories();
    this.pageService.getMainMenuPages().subscribe(result => this.mainMenuPages = result);
    this.initResizable();
    this.initializeViewedProducts();
    this.initializeFavoriteProducts();
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

  public recallRequest(event: any): void {
     event.preventDefault();
  }

  public toggleBottomPanel(event: any, bottomPanelType: string) {
    event.preventDefault();
    if (this.bottomPanelType === bottomPanelType) {
      this.bottomPanelType = null;
      jQuery(".rsec_content").removeClass("open");
      jQuery(".rsec_tab").css("display", "none");
    } else {
      this.bottomPanelType = bottomPanelType;
        jQuery(".rsec_content").addClass("open");
        jQuery(".rsec_tab").css("display", "block");
    }
  }

  private initResizable() {
    jQuery("#rsec_resizable").resizable({
      handles: {
        n: '#resizable-handle'
      },
      classes: {
        "ui-resizable-handle": "rsec_tyanya"
      },
      resize: function( event, ui ) {
        jQuery("#rsec_resizable").css("top", "0");
      }
    });
  }

  public getTabClass(bottomPanelType: string): string {
    if (this.bottomPanelType === bottomPanelType) {
      return "selected";
    }
    return "";
  }

  private initializeViewedProducts() {
    let viewedProductIds = this.getProductIdsFromLocalStorage("viewedProductIds");
    this.productService.loadSimplifiedProducts(viewedProductIds).subscribe(result => {
      this.viewedProducts = result;
    });
  }

  private initializeFavoriteProducts() {
    let favoriteProductIds = this.getProductIdsFromLocalStorage("favorites");
    this.productService.loadSimplifiedProducts(favoriteProductIds).subscribe(result => {
      this.favoriteProducts = result;
    });
  }

  private getProductIdsFromLocalStorage(groupId: string): number[] {
    let resultIds = [];
    let targetItem = localStorage.getItem(groupId);
    if (targetItem) {
      resultIds = JSON.parse(targetItem);
    }
    return resultIds;
  }

  private removeProductIdFromLocalStorage(groupId: string, productId: number) {
    let ids = this.getProductIdsFromLocalStorage(groupId);
    localStorage.setItem(groupId, JSON.stringify(ids.filter(x => x != productId)));
  }

  private addProductIdToLocalStorage(groupId: string, productId: number) {
    let ids = this.getProductIdsFromLocalStorage(groupId);
    localStorage.setItem(groupId, JSON.stringify(ids.push(productId)));
  }

  public favoriteLineMouseEnter(event: any) {
    var elem = event.target || event.srcElement;
    jQuery(elem.querySelector(".rsec_hov")).css("background-color", "#7A6137");
  }
  public favoriteLineMouseLeave(event: any) {
    var elem = event.target || event.srcElement;
    jQuery(elem.querySelector(".rsec_hov")).css("background-color", "white");
  }

  public removeFromFavorites(event: any, productId: number) {
    if (event) event.preventDefault();
    this.favoriteProducts = this.favoriteProducts.filter(x => x.id != productId);
    this.removeProductIdFromLocalStorage("favorites", productId);
  }

  public removeFromFavoritesMultiple(selectedOnly: boolean) {
    this.favoriteProducts.forEach(x => {
      if (selectedOnly) {
        if (x.selected) {
          this.removeFromFavorites(null, x.id);
        }
      } else {
        this.removeFromFavorites(null, x.id);
      }
    });
  }

}
