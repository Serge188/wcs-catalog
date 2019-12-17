import {AfterViewInit, Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {PriceType, ProductEntry} from "../_models/product-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
import {environment} from "../../environments/environment";
import {ProductsService} from "../products.service";
import {ProductSimplifiedEntry} from "../_models/product-simplified-entry";

@Component({
  selector: 'app-products-list',
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.css']
})
export class ProductsListComponent implements OnInit {

  @Input() products: ProductEntry[];

  public view: any = "CARD";
  public favoriteItemsIds: number[];
  public comparisonItemsIds: number[];
  public busketItemsCount: number;
  public busketItemsSum: number;
  public noImageUrl: string = environment.noImageUrl;

  constructor(private productService: ProductsService) {}

  ngOnInit() {
    this.favoriteItemsIds = JSON.parse(localStorage.getItem("favorites"));
    if (this.favoriteItemsIds == null) this.favoriteItemsIds = [];
    this.comparisonItemsIds = JSON.parse(localStorage.getItem("comparison"));
    if (this.comparisonItemsIds == null) this.comparisonItemsIds = [];
    this.busketItemsCount = JSON.parse(localStorage.getItem("busketItemsCount"));
    if (this.busketItemsCount == null) this.busketItemsCount = 0;
    this.busketItemsSum = JSON.parse(localStorage.getItem("busketItemsSum"));
    if (this.busketItemsSum == null) this.busketItemsSum = 0;
  }

  ngAfterContentInit() {
    if (!this.products) return;
    let favoriteIds: number[] = this.productService.getProductIdsFromLocalStorage("favorites");
    this.products.forEach(p => {
      if (favoriteIds.includes(p.id)) {
        p.favorite = true;
      }
    });
    let comparisonIds: number[] = this.productService.getProductIdsFromLocalStorage("comparison");
    this.products.forEach(p => {
      if (comparisonIds.includes(p.id)) {
        p.favorite = true;
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    console.log(changes);
    if (changes.products && changes.products.currentValue) {
      let favoriteIds: number[] = this.productService.getProductIdsFromLocalStorage("favorites");
      this.products.forEach(p => {
        if (favoriteIds.includes(p.id)) {
          p.favorite = true;
          console.log(p);
        }
      });
    }
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
        this.view = "LIST";
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

  public productHasOptions(product: ProductEntry): boolean {
    return product.saleOffers.length > 0;
  }

  public getCardImageLink(product: ProductEntry): string {
    if (product.offerCurrentImage && product.offerCurrentImage) {
      return product.offerCurrentImage.cardImageLink;
    }
    if (product.mainImage) {
      return product.mainImage.cardImageLink;
    }
    return null;
  }

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

  public getOptionsClass(p: ProductEntry): string {
    if (p.optionsAreImages) {
      return "is-pic";
    }
    return "";
  }

  public getOfferClass(p: ProductEntry, offer: SaleOfferEntry): string {
    if (p.currentOffer.id == offer.id) {
      return "selected";
    }
    return "";
  }

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

  public getOptionButtonStyle(offer: SaleOfferEntry):any {
    // if (offer.buttonImage && offer.buttonImage.optionImageLink) {
    //   let style = {'background-image': 'url(\'' + offer.buttonImage.optionImageLink + '\')'};
    //   return style;
    // }
    if (offer.optionValue.image && offer.optionValue.image.optionImageLink) {
      let style = {'background-image': 'url(\'' + offer.optionValue.image.optionImageLink + '\')'};
      return style;
    }
    return null;
  }

  public getProductUrl(product: ProductEntry): string {
    return "/product/" + product.alias;
  }

  public getPriceName(product: ProductEntry, price: number): string {
    switch (PriceType[product.priceType]) {
      case "NORMAL":
        return price + " руб.";
        break;
      case "PRICE_FROM":
        return "от " + price + " руб.";
        break;
      case "UNIT_PRICE_FROM":
        return "от " + price + " руб. за пог. м.";
        break;
      case "FROM_TO":
        return price + " руб.";
        break;
      case "PRICE_FOR_SET":
        return price + " руб. за комплект";
        break;
      default:
        return price + " руб.";
    }
  }

  private makeSimplifiedProductEntry(p: ProductEntry): ProductSimplifiedEntry {
    let entry = new ProductSimplifiedEntry();
    entry.id = p.id;
    entry.title = p.title;
    entry.alias = p.alias;
    if (p.mainImage) {
      entry.imageLink = p.mainImage.previewImageLink;
    }
    if (p.offerCurrentImage) {
      entry.imageLink = p.offerCurrentImage.previewImageLink;
    }
    entry.price = p.price;
    return entry;
  }

  private updateIdsList(param: boolean, list: number[], entryId: number): boolean {
    let result: boolean = false;
    if (param) {
      if (list.indexOf(entryId) == -1) {
        list.push(entryId);
        result = true;
      }
    } else {
      // list = list.filter(x => x!= entryId);
      let index = list.indexOf(entryId);
      if (index > -1 ) list.splice(index, 1);
    }
    return result;
  }
  public changeFavorite(event: any, p: ProductEntry) {
    event.preventDefault();
    let entry = this.makeSimplifiedProductEntry(p);
    entry.favorite = false;

    p.favorite = !p.favorite;
    if (this.updateIdsList(p.favorite, this.favoriteItemsIds, p.id)) {
      entry.favorite = true;
    }
    this.productService.$itemFavoriteAdded.emit(entry);
    localStorage.setItem("favorites", JSON.stringify(this.favoriteItemsIds));
  }

  public changeCompare(event: any, p: ProductEntry) {
    event.preventDefault();
    p.compared = !p.compared;
    let entry = this.makeSimplifiedProductEntry(p);
    entry.compared = false;
    if (this.updateIdsList(p.compared, this.comparisonItemsIds, entry.id)) {
      entry.compared = true;
    }
    this.productService.$itemComparisonAdded.emit(entry);
    localStorage.setItem("comparison", JSON.stringify(this.comparisonItemsIds));
  }

  public getIconClass(p: ProductEntry, param: boolean): string {
    if (param) {
      return "in";
    }
    return "";
  }

  public addItemToBusket(event: any, p: ProductEntry): void {
    event.preventDefault();
    this.busketItemsCount++;
    this.busketItemsSum = p.discountPrice != null ? this.busketItemsSum + p.discountPrice : this.busketItemsSum + p.price;
    localStorage.setItem("busketItemsCount", this.busketItemsCount.toString());
    localStorage.setItem("busketItemsSum", this.busketItemsSum.toString());
  }
}
