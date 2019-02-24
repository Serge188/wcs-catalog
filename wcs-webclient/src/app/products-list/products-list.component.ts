import {Component, Input, OnInit} from '@angular/core';
import {PriceType, ProductEntry} from "../_models/product-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";

@Component({
  selector: 'app-products-list',
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.css']
})
export class ProductsListComponent implements OnInit {

  @Input() products: ProductEntry[];


  public view: any = "CARD";

  constructor() {}

  ngOnInit() {
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

}
