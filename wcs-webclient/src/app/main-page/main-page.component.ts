import {Component, OnInit, ViewChild} from '@angular/core';
import {CategoriesService} from "../categories.service";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {CatalogView} from "../_models/catalog-view";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
import {FactoryEntry} from "../_models/factory-entry";
import {SideMenuComponent} from "../side-menu/side-menu.component";
import {PageService} from "../page.service";
import {PageEntry} from "../_models/page-entry";
import {PhotoGalleryItemEntry} from "../_models/photo-gallery-item-entry";
import {PhotoGalleryItemService} from "../photo-gallery-item.service";
import {FactoriesService} from "../factories.service";
declare var jQuery:any;
declare var ymaps:any;
declare var $:any;

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
  public sliderImagesLinks: string[];
  public photoGalleryItems: PhotoGalleryItemEntry[];
  public map: any;
  public mapZoom: number = 15;
  public mapCenterX: number = 54.514145;
  public mapCenterY: number = 36.253200;

  @ViewChild(SideMenuComponent)
  public sideMenu: SideMenuComponent;

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService,
              private brandService: FactoriesService,
              private pageService: PageService,
              private photoGalleryItemService: PhotoGalleryItemService) { }

  ngOnInit() {
    this.loadCategories();
    this.loadPopularProducts();
    this.loadPopularBrands();
    this.loadPages();
    this.loadPhotoGalleryItems();

    ymaps.ready().then(() => {
      this.map = new ymaps.Map('map', {
        center: [54.514145, 36.253200],
        zoom: 15
      });
      this.map.geoObjects
        .add(new ymaps.Placemark([54.513078, 36.263901], {
          preset: 'islands#icon',
          iconColor: '#0095b6'
        }))
        .add(new ymaps.Placemark([54.515617, 36.242300], {
          preset: 'islands#icon',
          iconColor: '#0095b6'
        }))
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
      jQuery(document).ready(function () {
        jQuery(".brands").owlCarousel({
          // center: true,
          items:8,
          loop: true,
          autoplay: true,
          slideTransition: 'linear',
          autoplayTimeout: 10000,
          autoplaySpeed: 10000,
          autoplayHoverPause: false
        });
      });
    })
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
        if (p.isSlider) {
          this.sliderPages.push(p);
          this.sliderImagesLinks.push(p.sliderImage.originalImageLink);
        }
        if (p.slider) this.sliderPages.push(p);
        if (p.showInMainMenu) this.mainMenuPages.push(p);
        if (p.showInSideMenu) this.sideMenuPages.push(p);
      }
      jQuery(document).ready(function(){
        jQuery(".slider-carousel").owlCarousel({
          center: true,
          items:1 ,
          loop:true,
          nav: true,
          autoplay: true,
          autoplaySpeed: 1000,
          navSpeed: 1000,
          navElement: "div",
          navText: ["<span><svg class=\"svg-icon\"><use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#svg-arrow-linear-left\"><svg viewBox=\"0 0 129 129\" id=\"svg-arrow-linear-left\">\n" +
          "        <path d=\"m88.6,121.3c0.8,0.8 1.8,1.2 2.9,1.2s2.1-0.4 2.9-1.2c1.6-1.6 1.6-4.2 0-5.8l-51-51 51-51c1.6-1.6 1.6-4.2 0-5.8s-4.2-1.6-5.8,0l-54,53.9c-1.6,1.6-1.6,4.2 0,5.8l54,53.9z\"></path>\n" +
          "\t</svg></use></svg></span>",
                    "<span><svg class=\"svg-icon\"><use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#svg-arrow-linear-right\"></use></svg></span>"]
        });
      });
    });
  }

  private loadPhotoGalleryItems() {
    this.photoGalleryItemService.getPhotoGalleryItems().subscribe(result => {
      this.photoGalleryItems = result;
      this.photoGalleryItems.forEach(i => {
        if (!i.mainImage && i.images && i.images.length > 0) {
          i.mainImage = i.images[0];
          i.images.splice(0, 1);
        }
      });
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

  public testProduct() {
    this.productsService.testProduct().subscribe();
  }

  public goToShop(event: any, coordX: number, coordY: number) {
    event.preventDefault();
    document.getElementById('map').innerHTML = '';
    this.map = new ymaps.Map('map', {
      center: [coordX, coordY],
      zoom: 17
    });
    this.map.geoObjects
      .add(new ymaps.Placemark([coordX, coordY], {
        preset: 'islands#icon',
        iconColor: '#0095b6'
      }));
  }
 }
