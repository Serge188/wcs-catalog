import { Component, OnInit } from '@angular/core';
import {ProductEntry} from "../_models/product-entry";
import {FactoryEntry} from "../_models/factory-entry";
import {ActivatedRoute} from "@angular/router";
import {FactoriesService} from "../factories.service";
import {ProductsService} from "../products.service";
import {SaleOfferEntry} from "../_models/sale-offer-entry";

@Component({
  selector: 'app-brand-page',
  templateUrl: './brand-page.component.html',
  styleUrls: ['./brand-page.component.css']
})
export class BrandPageComponent implements OnInit {

  public products: ProductEntry[] = [];
  public factory: FactoryEntry;
  public factoryAlias: string;

  constructor(
    private route: ActivatedRoute,
    private factoriesService: FactoriesService,
    private productsService: ProductsService) { }

  ngOnInit() {
    this.factoryAlias = this.route.snapshot.paramMap.get('brandAlias');
    this.loadFactoryByAlias();
    this.loadProductsByFactory();
  }

  private loadFactoryByAlias() {
    this.factoriesService.getBrandByAlias(this.factoryAlias).subscribe(result => {
      this.factory = result;
    });
  }

  private loadProductsByFactory() {
    this.productsService.getProductsByFactory(this.factoryAlias).subscribe(result => {
      this.products = result;
      for (let p of this.products) {
        this.calculateDiscount(p);
        if (p.saleOffers.length > 0) {
          this.setCurrentOffer(p, p.saleOffers[0]);
        }
      }
    });
  }

  private calculateDiscount(p: ProductEntry) {
    if (p.discountPrice) {
      let discount = (100 * (p.price - p.discountPrice)/p.price);
      if (discount < 1) {
        p.discount = (100 * (p.price - p.discountPrice)/p.price).toFixed(1);
      } else {
        p.discount = (100 * (p.price - p.discountPrice) / p.price).toFixed(0);
      }
    }
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
}
