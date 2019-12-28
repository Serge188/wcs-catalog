import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
import {FactoryEntry} from "../_models/factory-entry";
import {OfferOptionEntry} from "../_models/offer-option-entry";
import {CategoryFilter} from "../_models/category-filter";
declare var jQuery:any;

class ExtendedFactoryEntry extends FactoryEntry {
  selected: boolean = false;
}

class ExtendedOfferOptionEntry extends OfferOptionEntry {
  selected: boolean = false;
}

@Component({
  selector: 'app-category-page',
  templateUrl: './category-page.component.html',
  styleUrls: ['./category-page.component.css']
})

export class CategoryPageComponent implements OnInit {

  public category: CategoryEntry;
  categoryAlias: string;
  public products: ProductEntry[];
  public minPrice: number = 0;
  public minFilteredPrice: number = 0;
  public maxPrice: number = 0;
  public maxFilteredPrice: number = 0;
  public factories: ExtendedFactoryEntry[] = [];
  public optionsForFiltration: ExtendedOfferOptionEntry[];

  constructor(private categoriesService: CategoriesService,
              private productService: ProductsService,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.categoryAlias = this.route.snapshot.paramMap.get('categoryAlias');
    this.loadCategoryByAlias();
  }

  public loadCategoryByAlias(): void {
    this.categoriesService.getCategoryByAlias(this.categoryAlias).subscribe(result => {
      this.category = result;
      this.loadCategoryProducts(null);
      this.loadPossibleFilterOptions();
    })
  }

  public getSubCategoryLink(subCat: CategoryEntry) {
    return "/catalog/" + this.category.alias + "/" + subCat.alias;
  }

  public loadCategoryProducts(filter: CategoryFilter): void {
    this.productService.getCategoryProducts(this.category.id, filter).subscribe(result => {
      this.products = result;
      for (let p of this.products) {
        this.calculateDiscount(p);
        if (p.saleOffers.length > 0) {
          this.setCurrentOffer(p, p.saleOffers[0]);
        }
      }
      if (this.products && this.products.length > 0) {
        if (this.products && this.products.length > 0 && !this.products.find(x => x.price == null)) {
          this.minPrice = this.products[0].price;
          for (let p of this.products) {
            if (p.price < this.minPrice) this.minPrice = p.price;
          }
        }
        this.maxPrice = this.products[0].price;
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
    let filter: CategoryFilter = new CategoryFilter();
    filter.minPrice = this.minFilteredPrice;
    filter.maxPrice = this.maxFilteredPrice;
    filter.factoryIds = [];
    filter.options = [];

    for (let f of this.factories) {
      if (f.selected) filter.factoryIds.push(f.id);
    }

    for (let option of this.optionsForFiltration) {
      let selectedValues = option.values.filter(x => x.selected == true);
      if (selectedValues.length > 0) {
        let optionForServer: OfferOptionEntry = new OfferOptionEntry();
        optionForServer.id = option.id;
        optionForServer.values = selectedValues;
        filter.options.push(optionForServer);
      }
    }
    this.loadCategoryProducts(filter);
  }

  public factoryFilterChanged(f: ExtendedFactoryEntry) {
    // let factoryAmongSelected = this.selectedFactories.find(x => x.id == f.id);
    // if (!factoryAmongSelected) {
    //   this.selectedFactories.push(f);
    // } else {
    //   let index = this.selectedFactories.indexOf(factoryAmongSelected);
    //   if (index != -1) this.selectedFactories.splice(index, 1);
    // }
    this.applyFilter();
  }

  public toggleFilterBlock(event: any): void {
    event.preventDefault();
    console.log(event);
    event.path.forEach(x => {
      if (x.classList && x.classList.contains("lvl1")) {
        if (x.classList.contains("closed")) {
          x.classList.remove("closed");
        } else {
          x.classList.add("closed");
        }
      }
    });
  }

  public loadPossibleFilterOptions(): void {
    this.categoriesService.getPosssibleFilterOptions(this.category.id).subscribe(result => {
      this.optionsForFiltration = result;
    });

    this.categoriesService.getPricesRange(this.category.id).subscribe(result => {
      if (result.length == 2) {
        if (result[0] < result[1]) {
          this.minPrice = result[0];
          this.maxPrice = result[1];
          this.minFilteredPrice = result[0];
          this.maxFilteredPrice = result[1];
        } else {
          this.minPrice = result[1];
          this.maxPrice = result[0];
          this.minFilteredPrice = result[1];
          this.maxFilteredPrice = result[0];
        }
      }
      let minPrice = jQuery(".min-price");
      let maxPrice = jQuery(".max-price");
        jQuery("#slider-range").slider({
          range: true,
          min: this.minPrice,
          max: this.maxPrice,
          values: [this.minFilteredPrice, this.maxFilteredPrice],
          slide: (event, ui) => {
            minPrice.val(ui.values[0]);
            maxPrice.val(ui.values[1]);
            this.minFilteredPrice = ui.values[0];
            this.maxFilteredPrice = ui.values[1];
            this.applyFilter();
          }
        });
    });

    this.categoriesService.getFactoriesInCategory(this.category.id).subscribe(result => this.factories = result);
  }

}
