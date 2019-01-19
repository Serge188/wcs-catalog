import { Component, OnInit } from '@angular/core';
import {ProductsService} from "../products.service";
import {ActivatedRoute} from "@angular/router";
import {ProductEntry} from "../_models/product-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";

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

  constructor(private productService: ProductsService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.productAlias = this.route.snapshot.paramMap.get('alias');
    this.loadProduct()
  }

  private loadProduct(): void {
    this.productService.getProductByAlias(this.productAlias).subscribe(result => {
      this.product = result;
      this.calculateDiscount();
      if (this.product.discountPrice) {
        this.economy = this.product.price - this.product.discountPrice;
      }
      if (this.product.saleOffers.length > 0) {
        this.productHasOptions = true;
        this.product.currentOffer = this.product.saleOffers[0];
      }
      console.log(this.product);
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
}
