import { Component, OnInit } from '@angular/core';
import {CategoryEntry} from "../_models/category-entry";
import {CategoriesService} from "../categories.service";
import {PageService} from "../page.service";
import {PageEntry} from "../_models/page-entry";
import {ProductSimplifiedEntry} from "../_models/product-simplified-entry";
import {ProductsService} from "../products.service";
import {IdToIdEntry} from "../_models/id-to-id-entry";
import {IdQtyEntry} from "../_models/id-qty-entry";
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
  public comparisonProducts: ProductSimplifiedEntry[] = [];
  public busketItems: ProductSimplifiedEntry[] = [];
  public busketItemsCount: number;
  public busketItemsSum: number;

  constructor(
    private categoriesService: CategoriesService,
    private pageService: PageService,
    private productService: ProductsService) {
    this.productService.$itemFavoriteAdded.subscribe(entry => {
      if (entry.favorite) {
        this.favoriteProducts.push(entry);
      } else {
        this.favoriteProducts = this.favoriteProducts.filter(x => x.id != entry.id);
      }
    });
    this.productService.$itemComparisonAdded.subscribe(entry => {
      if (entry.compared) {
        this.comparisonProducts.push(entry);
      } else {
        this.comparisonProducts = this.comparisonProducts.filter(x => x.id != entry.id);
      }
    });
    this.productService.$itemBusketAdded.subscribe(entry => {
      this.busketItems.push(entry);
      this.recalculateBusketItems();
    });
  }

  ngOnInit() {
    this.loadCategories();
    this.pageService.getMainMenuPages().subscribe(result => this.mainMenuPages = result);
    this.initResizable();
    this.initializeViewedProducts();
    this.initializeFavoriteProducts();
    this.initializeComparisonProducts();
    this.initializeBusketItems();
    this.busketItemsCount = JSON.parse(localStorage.getItem("busketItemsCount"));
    if (this.busketItemsCount == null) this.busketItemsCount = 0;
    this.busketItemsSum = JSON.parse(localStorage.getItem("busketItemsSum"));
    if (this.busketItemsSum == null) this.busketItemsSum = 0;
    // this.recalculateBusketItems();
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
    if (this.bottomPanelType === bottomPanelType || bottomPanelType == null) {
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
    let viewedProductIds = this.productService.getProductIdsFromLocalStorage("viewedProductIds");
    this.productService.loadSimplifiedProducts(viewedProductIds).subscribe(result => {
      this.viewedProducts = result;
    });
  }

  private initializeFavoriteProducts() {
    let favoriteProductIds = this.productService.getProductIdsFromLocalStorage("favorites");
    this.productService.loadSimplifiedProducts(favoriteProductIds).subscribe(result => {
      this.favoriteProducts = result;
    });
  }

  private initializeComparisonProducts() {
    let comparisonProductIds = this.productService.getProductIdsFromLocalStorage("comparison");
    this.productService.loadSimplifiedProducts(comparisonProductIds).subscribe(result => {
      this.comparisonProducts = result;
    });
  }

  private initializeBusketItems() {
    let busketItemIdEntries: IdToIdEntry[] = JSON.parse(localStorage.getItem("busketItems"));
    if (busketItemIdEntries && busketItemIdEntries.length > 0) {
      this.productService.loadSimplifiedProductsWithOffers(busketItemIdEntries).subscribe(result => {
        if (result) {
          this.busketItems = result;
          this.recalculateBusketItems();
        }
      });
      this.busketItems.forEach(item => {
        let idEntry = busketItemIdEntries.find(i => i.primaryId == item.id);
      });
    }
  }

  public productLineMouseEnter(event: any) {
    var elem = event.target || event.srcElement;
    jQuery(elem.querySelector(".rsec_hov")).css("background-color", "#7A6137");
  }
  public productLineMouseLeave(event: any) {
    var elem = event.target || event.srcElement;
    jQuery(elem.querySelector(".rsec_hov")).css("background-color", "white");
  }

  public removeFromBottomPanel(event: any, product: ProductSimplifiedEntry, list: ProductSimplifiedEntry[], listId: string) {
    if (event) event.preventDefault();
    let index = list.indexOf(product);
    if (index > -1) {
      list.splice(index, 1);
    }
    this.productService.removeProductIdFromLocalStorage(listId, product.id);
  }

  public removeFromBottomPanelMultiple(selectedOnly: boolean, list: ProductSimplifiedEntry[], listId: string) {
    if (!selectedOnly) {
      list.splice(0, list.length);
      this.productService.clearLocalStorageForGroupId(listId);
    } else {
      list.forEach(x => {
        if (selectedOnly) {
          if (x.selected) {
            this.removeFromBottomPanel(null, x, list, listId);
          }
        }
      });
    }
  }

  public changeProductQty(event: any, p: ProductSimplifiedEntry, increase: boolean) {
    event.preventDefault();
    if (!p.qty) p.qty = 1;
    if (increase) {
      p.qty++;
    } else {
      p.qty--;
    }
    p.sum = p.price * p.qty;
    this.recalculateBusketItems();
  }

  private makeIdQtyEntry(p: ProductSimplifiedEntry): IdQtyEntry {
    console.log(p);
    let entry: IdQtyEntry = new IdQtyEntry();
    entry.primaryId = p.id;
    entry.secondaryId = p.currentSaleOfferId;
    entry.qty = p.qty;
    entry.sum = p.sum;
    return entry;
  }

  private recalculateBusketItems() {
    let sum = 0;
    let count = 0;
    // let busketEntries: IdQtyEntry[] = [];
    this.busketItems.forEach(i => {
      sum += i.price * i.qty;
      count += i.qty;
      // busketEntries.push(this.makeIdQtyEntry(i));
    });
    this.busketItemsSum = sum;
    this.busketItemsCount = count;
    // localStorage.setItem("busketItems", JSON.stringify(busketEntries));
    localStorage.setItem("busketItemsCount", this.busketItemsCount.toString());
    localStorage.setItem("busketItemsSum", this.busketItemsSum.toString());
  }

  public removeItemFromBasket(event: any, p: ProductSimplifiedEntry) {
    if (event) event.preventDefault();
    let index = this.busketItems.indexOf(p);
    if (index > -1) {
      this.busketItems.splice(index, 1);
    }
    this.recalculateBusketItems();
    this.removeBusketItemFromStorage(p);
  }

  private removeBusketItemFromStorage(p: ProductSimplifiedEntry) {
    let entries: IdToIdEntry[] = JSON.parse(localStorage.getItem("busketItems"));
    if (entries && entries.length > 0) {
      let entry = entries.find(x => x.primaryId == p.id && (!x.secondaryId || x.secondaryId == p.currentSaleOfferId));
      if (entry) {
        let index = entries.indexOf(entry);
        entries.splice(index, 1);
      }
      localStorage.setItem("busketItems", JSON.stringify(entries));
    }
  }

  public removeMultipleItemsFromBusket(event: any, selectedOnly: boolean) {
    if (selectedOnly) {
      this.busketItems.forEach(bi => {
        if (bi.selected) {
          this.removeBusketItemFromStorage(bi);
        }
      });
      this.busketItems = this.busketItems.filter(x => !x.selected);
    } else {
      this.busketItems = [];
      localStorage.setItem("busketItems", JSON.stringify([]));
      localStorage.setItem("busketItemsCount", "0");
      localStorage.setItem("busketItemsSum", "0");
    }
    this.recalculateBusketItems();
  }
}
