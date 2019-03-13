import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
import {FactoryEntry} from "../_models/factory-entry";
declare var jQuery:any;

@Component({
  selector: 'app-category-page',
  templateUrl: './category-page.component.html',
  styleUrls: ['./category-page.component.css']
})
export class CategoryPageComponent implements OnInit {

  public category: CategoryEntry;
  categoryAlias: string;
  public products: ProductEntry[];
  public filteredProducts: ProductEntry[];
  public minPrice: number = 0;
  public maxPrice: number = 0;
  public factories: FactoryEntry[] = [];
  public selectedFactories: FactoryEntry[] = [];

  constructor(private categoriesService: CategoriesService,
              private productService: ProductsService,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.categoryAlias = this.route.snapshot.paramMap.get('categoryAlias');
    this.loadCategoryByAlias();
    // jQuery(document).ready(function(){
    //   jQuery(".aroundslider").slider({
    //     range: true,
    //     min: 0,
    //     max: 500,
    //     values: [ 75, 300 ],
    //     slide: function( event, ui ) {
    //       jQuery( ".min-price" ).val( "$" + ui.values[ 0 ] + " - $" + ui.values[ 1 ] );
    //     }
    //   });
    //   jQuery( "#amount" ).val( "$" + jQuery( ".aroundslider" ).slider( "values", 0 ) +
    //     " - $" + jQuery( ".aroundslider" ).slider( "values", 1 ) );
    // });
  }

  public loadCategoryByAlias(): void {
    this.categoriesService.getCategoryByAlias(this.categoryAlias).subscribe(result => {
      this.category = result;
      this.loadCategoryProducts();
    })
  }

  public getSubCategoryLink(subCat: CategoryEntry) {
    return "/catalog/" + this.category.alias + "/" + subCat.alias;
  }

  public loadCategoryProducts(): void {
    this.productService.getCategoryProducts(this.category.id).subscribe(result => {
      this.products = result;
      this.filteredProducts = result;
      for (let p of this.products) {
        // p.link = "/catalog/mebel_dlya_kabineta/kresla_office/kreslo_van_gog/";
        this.calculateDiscount(p);
        if (p.saleOffers.length > 0) {
          this.setCurrentOffer(p, p.saleOffers[0]);
        }
      }
      if (!this.products.find(x => x.price == null)) {
        this.minPrice = this.products[0].price;
        for (let p of this.products) {
          if (p.price < this.minPrice) this.minPrice = p.price;
        }
      }
      for (let p of this.products) {
        if (p.price > this.maxPrice) this.maxPrice = p.price;
        let f = this.factories.find(x => x.id == p.factory.id);
        if (!f) {
          this.factories.push(p.factory);
        }
      }
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

  public applyFilter() {
    this.filteredProducts = this.products.filter(x => x.price >= this.minPrice && x.price <= this.maxPrice);
    if (this.selectedFactories.length > 0) {
      this.filteredProducts = this.filteredProducts.filter(x => {
        for (let sf of this.selectedFactories) {
          if (sf.id == x.factory.id) return true;
        }
      });
    }
  }

  public factoryFilterChanged(f: FactoryEntry) {
    let factoryAmongSelected = this.selectedFactories.find(x => x.id == f.id);
    if (!factoryAmongSelected) {
      this.selectedFactories.push(f);
    } else {
      let index = this.selectedFactories.indexOf(factoryAmongSelected);
      if (index != -1) this.selectedFactories.splice(index, 1);
    }
    this.applyFilter();
    console.log(this.selectedFactories);
  }

  public toggleFilterBlock(event: any): void {
    event.preventDefault();
  }

}
