import { Component, OnInit } from '@angular/core';
import {ProductsService} from "../products.service";
import {ActivatedRoute} from "@angular/router";
import {PriceType, ProductEntry} from "../_models/product-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
declare var jQuery:any;

@Component({
  selector: 'app-product-page',
  templateUrl: './product-page.component.html',
  styleUrls: ['./product-page.component.css']
})
export class ProductPageComponent implements OnInit {

  public productAlias: string;
  public product: ProductEntry = {};
  public economy: number;
  public showProperties: boolean = false;
  public optionsOpen: boolean = false;
  public productHasOptions: boolean = false;
  public busketItemsCount: number;
  public busketItemsSum: number;
  public productQty: number = 1;

  constructor(private productService: ProductsService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.productAlias = this.route.snapshot.paramMap.get('alias');
    this.loadProduct();
    this.busketItemsCount = JSON.parse(localStorage.getItem("busketItemsCount"));
    if (this.busketItemsCount == null) this.busketItemsCount = 0;
    this.busketItemsSum = JSON.parse(localStorage.getItem("busketItemsSum"));
    if (this.busketItemsSum == null) this.busketItemsSum = 0;
    jQuery(document).ready(function(){
      jQuery(".fancybox").fancybox({
        openEffect: "elastic",
        closeEffect: "none"
      });
    });
  }

  private loadProduct(): void {
    this.productService.getProductByAlias(this.productAlias).subscribe(result => {
      this.product = result;
      if (!this.product.price && this.product.saleOffers.length > 0) {
        this.product.price = this.product.saleOffers[0].price;
        this.product.discountPrice = this.product.saleOffers[0].discountPrice;
      }
      this.calculateDiscount();
      if (this.product.discountPrice) {
        this.economy = this.product.price - this.product.discountPrice;
      }
      if (this.product.saleOffers.length > 0) {
        this.productHasOptions = true;
        this.product.currentOffer = this.product.saleOffers[0];
      }
    });
  }

  private calculateDiscount(): void {
    if (this.product.discountPrice) {
      let discount = (100 * (this.product.price - this.product.discountPrice)/this.product.price);
      if (discount < 1) {
        this.product.discount = (100 * (this.product.price - this.product.discountPrice)/this.product.price).toFixed(1);
      } else {
        this.product.discount = (100 * (this.product.price - this.product.discountPrice) / this.product.price).toFixed(0);
      }
    }
  }

  public changeTab(tab: string, event: any): void {
    event.preventDefault();
    let descTab: HTMLElement = document.getElementById("description-tab");
    let propTab: HTMLElement = document.getElementById("properties-tab");
    switch (tab) {
      case "description":
        descTab.classList.add("active");
        propTab.classList.remove("active");
        this.showProperties = false;
      break;
      case "properties":
        descTab.classList.remove("active");
        propTab.classList.add("active");
        this.showProperties = true;
      break;
    }
  }

  public switchOptionsWindow(): void {
    let optionsSelector: HTMLElement = document.getElementById("options-selector");
    let optionsContainer: HTMLElement = document.getElementById("optionsContainer");
    if (!optionsSelector.classList.contains("open")) {
      this.optionsOpen = true;
      this.openOptionsWindow(optionsSelector);
      document.addEventListener('click', function($event){
        if (!optionsContainer.contains($event.srcElement)) {
          optionsSelector.classList.remove("open");
          optionsSelector.classList.add("close");
        }
      });
    } else {
      this.closeOptionsWindow(optionsSelector);
      this.optionsOpen = false;
    }
  }

  private openOptionsWindow(optionsSelector: HTMLElement): void {
    optionsSelector.classList.add("open");
    optionsSelector.classList.remove("close");

  }

  private closeOptionsWindow(optionsWindow: HTMLElement): void {
    optionsWindow.classList.remove("open");
    optionsWindow.classList.add("close");

  }

  public selectOption(offer: SaleOfferEntry): void {
    this.product.currentOffer = offer;
    if (offer.mainImage) {
      this.product.offerCurrentImage = offer.mainImage;
    }
    this.product.price = offer.price;
    this.product.discountPrice = offer.discountPrice;
    this.calculateDiscount();
  }

  public getOptionButtonStyle(offer: SaleOfferEntry):any {
    if (offer.buttonImage && offer.buttonImage.optionImageLink) {
      let style = {'background-image': 'url(\'' + offer.buttonImage.optionImageLink + '\')'};
      return style;
    }
    return null;
  }

  public getOptionButtonClass(offer: SaleOfferEntry): string {
    if (offer.buttonImage && offer.buttonImage.optionImageLink) {
      return "c-attributes__value-pic"
    }
    return "";
  }

  public getBaseImageLink(): string {
    if (this.product.offerCurrentImage) {
      return this.product.offerCurrentImage.baseImageLink;
    }
    return this.product.mainImage.baseImageLink;
  }

  public getOriginalImageLink(): string {
    if (this.product.offerCurrentImage) {
      return this.product.offerCurrentImage.originalImageLink;
    }
    return this.product.mainImage.originalImageLink;
  }

  public showAdditionalImages(): boolean {
    if (this.product.images && this.product.images.length > 0) {
      return true;
    }
    return false;
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

  public scrollToElement(event: any, elementId: string): void {
    event.preventDefault();
    if (elementId == "detailtext") this.changeTab("description", event);
    if (elementId == "properties") this.changeTab("properties", event);
    jQuery([document.documentElement, document.body]).animate({
      scrollTop: jQuery(".nav-tabs").offset().top
    }, 500);
  }

  public addItemToBusket(event: any): void {
    event.preventDefault();
    this.busketItemsCount = this.busketItemsCount + this.productQty ;
    this.busketItemsSum = this.product.discountPrice != null ? this.busketItemsSum + this.product.discountPrice * this.productQty : this.busketItemsSum + this.product.price * this.productQty;
    localStorage.setItem("busketItemsCount", this.busketItemsCount.toString());
    localStorage.setItem("busketItemsSum", this.busketItemsSum.toString());
  }

  public minusQty(event: any) {
    event.preventDefault();
    if (this.productQty > 0) this.productQty--;
  }

  public plusQty(event: any) {
    event.preventDefault();
    this.productQty++;
  }
}
