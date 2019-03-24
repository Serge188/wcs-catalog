import {Component, OnInit, ViewChild} from '@angular/core';
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {CatalogView} from "../_models/catalog-view";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
import {BrandService} from "../brand.service";
import {FactoryEntry} from "../_models/factory-entry";
import {SideMenuComponent} from "../side-menu/side-menu.component";
import {PageService} from "../page.service";
import {PageEntry} from "../_models/page-entry";
declare var jQuery:any;

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  public categories: CategoryEntry[] = [];
  public products: ProductEntry[];
  public view: any = "CARD";
  public popularCategories: CategoryEntry[] = [];
  public popularCategoriesMap: Map<number, CategoryEntry[]> = new Map();
  public factories: FactoryEntry[];
  public pages: PageEntry[];
  public sliderPages: PageEntry[] = [];
  public sideMenuPages: PageEntry[] = [];
  public mainMenuPages: PageEntry[] = [];

  @ViewChild(SideMenuComponent)
  public sideMenu: SideMenuComponent;

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService,
              private brandService: BrandService, private pageService: PageService) { }

  ngOnInit() {
    this.loadCategories();
    this.loadPopularProducts();
    this.loadPopularBrands();
    this.loadPages();
    jQuery(document).ready(function(){
      jQuery(".owl-carousel").owlCarousel({
        center: true,
        items:1,
        loop:true,
        margin:10,
        nav: true,
        autoplay: true
      });
    });
  }

  public loadCategories(): void {
    this.categoriesService.getCategoriesWithProductsCount().subscribe(result => {
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
        if (cat.popular) {
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

  private loadPopularProducts(): void {
    this.productsService.getPopularProducts().subscribe(result => {
      this.products = result;
      for (let p of this.products) {
        p.link = "/catalog/mebel_dlya_kabineta/kresla_office/kreslo_van_gog/";
        this.calculateDiscount(p);
        if (p.saleOffers.length > 0) {
          this.setCurrentOffer(p, p.saleOffers[0]);
        }
      }
    });
  }

  private loadPopularBrands(): void {
    this.brandService.getPopularBrands().subscribe(result => {
      this.factories = result;
      for (let f of this.factories) {
        f.link = "/brands/" + f.alias;
      }
      console.log(this.factories);
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

  public loadPages(): void {
    this.pageService.getPages().subscribe(result => {
      this.pages = result;
      for (let p of this.pages) {
        if (p.isSlider) this.sliderPages.push(p);
        if (p.showInMainMenu) this.mainMenuPages.push(p);
        if (p.showInSideMenu) this.sideMenuPages.push(p);
      }
    });
  }

  public getSliderBackground(page: PageEntry): any {
    return {"background-image":"url('" + page.sliderImage.originalImageLink + "')"};
  }

  public getParentPage(page: PageEntry): PageEntry {
    if (page.parentPageId) {
      return this.pages.find(p => p.id == page.parentPageId);
    }
    return null;
  }
 }
