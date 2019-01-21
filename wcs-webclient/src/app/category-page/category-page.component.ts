import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";

@Component({
  selector: 'app-category-page',
  templateUrl: './category-page.component.html',
  styleUrls: ['./category-page.component.css']
})
export class CategoryPageComponent implements OnInit {

  public category: CategoryEntry;
  categoryAlias: string;
  public products: ProductEntry[];

  constructor(private categoriesService: CategoriesService,
              private productService: ProductsService,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.categoryAlias = this.route.snapshot.paramMap.get('categoryAlias');
    this.loadCategoryByAlias();

  }

  public loadCategoryByAlias(): void {
    this.categoriesService.getCategoryByAlias(this.categoryAlias).subscribe(result => {
      this.category = result;
      this.loadCategoryProducts();
      console.log(this.category);
    })
  }

  public getSubCategoryLink(subCat: CategoryEntry) {
    return "/catalog/" + this.category.alias + "/" + subCat.alias;
  }

  public loadCategoryProducts(): void {
    console.log("1");
    this.productService.getCategoryProducts(this.category.id).subscribe(result => {
      this.products = result;
      for (let p of this.products) {
        p.link = "/catalog/mebel_dlya_kabineta/kresla_office/kreslo_van_gog/";
        this.calculateDiscount(p);
        if (p.saleOffers.length > 0) {
          this.setCurrentOffer(p, p.saleOffers[0]);
        }
      }
      console.log(this.products);
    });
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

}
