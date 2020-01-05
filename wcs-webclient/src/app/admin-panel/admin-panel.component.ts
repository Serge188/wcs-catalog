import { Component, OnInit } from '@angular/core';
import {CategoriesService} from "../categories.service";
import {AppComponent} from "../app.component";
import {CategoryEntry} from "../_models/category-entry";
import {ProductsService} from "../products.service";
import {ProductEntry} from "../_models/product-entry";
import {Observable} from "rxjs/internal/Observable";
import {OfferOptionEntry} from "../_models/offer-option-entry";
import {OptionsService} from "../options.service";
import {OptionValueEntry} from "../_models/option-value-entry";
import {SaleOfferEntry} from "../_models/sale-offer-entry";
import {AuthenticationService} from "../authentication.service";
import {FactoriesService} from "../factories.service";
import {FactoryEntry} from "../_models/factory-entry";
import {PageService} from "../page.service";
import {PageEntry} from "../_models/page-entry";
import {ImageEntry} from "../_models/image-entry";
import {PhotoGalleryItemEntry} from "../_models/photo-gallery-item-entry";
import {PhotoGalleryItemService} from "../photo-gallery-item.service";
import * as toastr from 'toastr';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  public categories: CategoryEntry[];
  public topLevelCategories: CategoryEntry[] = [];
  public itemsInList: any[] = [];
  public newCategory: CategoryEntry = {};
  public newProduct: ProductEntry = {};
  public view: any;
  public activeCategory: CategoryEntry;
  public offerOptions: OfferOptionEntry[] = [];
  public newProductSelectedOption: OfferOptionEntry = {};
  public newProductSelectedOptionId: number;
  public newProductSelectedOptionValue: string = "";
  public newProductCategoryId: number;
  public editingOption: OfferOptionEntry = {};
  public newProductSaleOffersOptionId: number;
  public newProductSaleOfferOption: OfferOptionEntry;

  public factories: FactoryEntry[] = [];
  public newFactory: FactoryEntry = {};

  public pages: PageEntry[] = [];
  public topLevelPages: PageEntry[] = [];
  public newPageActive: boolean = false;
  public photoGalleryItems: PhotoGalleryItemEntry[] = [];
  public activeGalleryItem: PhotoGalleryItemEntry;

  private SAVED_SUCCESSFULLY: string = "Успешно сохранено";
  private SAVING_ERROR: string = "Ошибка при сохранении";
  private REMOVED_SUCCESSFULLY: string = "Успешно удалено";
  private REMOVING_ERROR: string = "Ошибка при удалении";

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService,
              private optionsService: OptionsService,
              private authenticationService: AuthenticationService,
              private factoriesService: FactoriesService,
              private pageService: PageService,
              private photoGalleryItemService: PhotoGalleryItemService) { }

  ngOnInit() {
    this.view = "categories";
    this.loadCategories();
    this.loadProductOptions();
    this.loadFactories();
    this.loadPages();
    this.loadPhotoGalleryItems();
  }

  public loadCategories(): void {
    this.itemsInList = [];
    this.topLevelCategories = [];
    this.categoriesService.getCategories().subscribe(result => {
      this.categories = result;
      for (let cat of this.categories) {
        cat.expanded = false;
        cat.selected = false;
        if (!cat.parentCategoryId) {
          cat.level = 1;
          this.itemsInList.push(cat);
          this.topLevelCategories.push(cat);
        }
      }
    });
  }

  public loadProductsForActiveCategory(): void {
    if (this.activeCategory && this.activeCategory.id) {
      this.productsService.getOneLevelCategoryProducts(this.activeCategory.id).subscribe(result => {
        this.activeCategory.products = result;
        // let index = this.itemsInList.indexOf(cat, 0);
        // if (index != -1) {
        //   for (let p of cat.products) {
        //     index++;
        //     this.itemsInList.splice(index, 0, p);
        //     p.level = cat.level + 1;
        //   }
        // }
      });
    }

  }

  public loadProductOptions(): void {
    this.optionsService.getProductOptions().subscribe(result => {
      this.offerOptions = result;
    })
  }

  private loadPhotoGalleryItems() {
    this.photoGalleryItemService.getPhotoGalleryItems().subscribe(result => {
      this.photoGalleryItems = result;
    });
  }

  public createOrUpdateGalleryItem(item: PhotoGalleryItemEntry) {
    this.photoGalleryItemService.createOrUpdateGalleryItem(item).subscribe(() => {
      toastr.success(this.SAVED_SUCCESSFULLY);
      this.loadPhotoGalleryItems();
    }, () => toastr.error(this.SAVING_ERROR));
  }

  public deleteGalleryItem(item: PhotoGalleryItemEntry) {
    if (confirm("Вы уверены?")) {
      if (item.id) {
        this.photoGalleryItemService.deleteGalleryItem(item.id).subscribe(() => {
          toastr.success(this.REMOVED_SUCCESSFULLY);
          this.loadPhotoGalleryItems();
        }, () => toastr.error(this.REMOVING_ERROR));
      }
    }
  }

  public deleteImageFromGalleryItem(event: any, imageId: number) {
    event.preventDefault();
    if (confirm("Вы уверены?")) {
      this.photoGalleryItemService.deleteImageFromGalleryItem(imageId).subscribe(
        () => {
          toastr.success(this.REMOVED_SUCCESSFULLY)
        }, () => toastr.error(this.REMOVING_ERROR));
    }
  }

  public removeProductsFromList(category: CategoryEntry): void {
    for (let p of category.products) {
      let index = this.itemsInList.indexOf(p, 0);
      if (index != -1) {
        this.itemsInList.splice(index, 1);
      }
    }
  }

  public getActiveClass(category: CategoryEntry) {
    if (category.selected) {
      return "active";
    }
    return "";
  }

  public toggleCategory(category: CategoryEntry) {
    if (!category.selected) {
      for (let cat of this.categories) {
        cat.selected = false;
      }
      category.selected = true;
      this.activeCategory = category;
    }
    if (!category.expanded) {
      this.expandCategory(category);
    } else {
      this.collapseCategory(category);
    }
  }

  private expandCategory(category: CategoryEntry): void {
    category.expanded = true;
    let count = 1;
    for (let cat of this.categories) {
      if (cat.parentCategoryId == category.id) {
        let index = this.itemsInList.indexOf(category, 0);
        if (index != -1) {
          this.itemsInList.splice(index + count, 0, cat);
          cat.level = category.level + 1;
          cat.expanded = false;
          cat.selected = false;
          count++;
        }
      }
    }
    this.loadProductsForActiveCategory();
  }

  private collapseCategory(category: CategoryEntry):void {
    category.expanded = false;
    for (let cat of this.categories) {
      if (cat.parentCategoryId == category.id) {
        // if (cat.products && cat.products.length > 0) {
        //   this.removeProductsFromList(cat);
        // }
        cat.expanded = false;
        let index = this.itemsInList.indexOf(cat, 0);
        if (index != -1) {
          this.itemsInList.splice(index, 1);
        }
      }
    }
    // this.removeProductsFromList(category);
    category.expanded = false;
  }

  public getPadding(entry: any): any {
      let padding: any = {};
      if (entry.level == 1) {
        padding['font-weight'] = 'bold';
      }
      if (entry.level > 1) {
        let value = (entry.level - 1) * 45;
        padding['padding-left'] = value + 'px';
      }
      return padding;
  }

  public openModalCategory(event: any, item: any, createChild: boolean): void {
    event.preventDefault();
    this.openModal("modal-category");
    if (item) {
      if (createChild) {
        this.newCategory.parentCategoryId = item.id;
      } else {
        this.newCategory.id = item.id;
        this.newCategory.title = item.title;
        this.newCategory.parentCategoryId = item.parentCategoryId;
        this.newCategory.description = item.description;
        this.newCategory.popular = item.popular;
        this.newCategory.image = item.image;
      }
    }
  }

  public openModalProduct(event: any, product: ProductEntry) {
    this.openModal("modal-product");
    if (product) {
      this.newProduct.id = product.id;
      this.newProduct.categoryId = product.category.id;
      this.newProduct.category = product.category;
      this.newProduct.price = product.price;
      this.newProduct.discountPrice = product.discountPrice;
      this.newProduct.title = product.title;
      // this.newProduct.description = product.description;
      if (product.description) {
        this.newProduct.description = product.description
          .split("<br/>").join("\n")
          .split("&nbsp;&nbsp;&nbsp;").join("\t")
          .split("&nbsp;").join(" ");
      }
      this.newProduct.mainImage = product.mainImage;
      this.newProduct.images = product.images;
      this.newProduct.saleOffers = product.saleOffers;
      this.newProduct.options = product.options;
      this.newProduct.priceType = product.priceType;
      this.newProduct.factoryId = product.factoryId;
      this.newProduct.productOfDay = product.productOfDay;
      this.newProduct.newProduct = product.newProduct;
      this.newProduct.hit = product.hit;
      this.newProduct.promo = product.promo;
      this.newProduct.popular = product.popular;

      if (product.saleOffers && product.saleOffers.length > 0) {
        this.newProductSaleOffersOptionId = product.saleOffers[0].offerOption.id;
        this.newProductSaleOfferOption = this.offerOptions.find(x => x.id == this.newProductSaleOffersOptionId);
        for (let so of this.newProduct.saleOffers) {
          so.optionValueId = so.optionValue.id;
        }
      }
    } else if (this.activeCategory) {
      this.newProduct.categoryId = this.activeCategory.id;
    }
  }

  private openModal(modalId: string): void {
    let mask: HTMLElement = document.getElementById("cover-mask");
    let form: HTMLElement = document.getElementById(modalId);
    mask.classList.add("modal-visible");
    mask.classList.remove("modal-hidden");
    form.classList.add("modal-visible");
    form.classList.remove("modal-hidden");
  }

  public closeModal() {
    let elements: HTMLElement[] = [];
    elements.push(document.getElementById("modal-category"));
    elements.push(document.getElementById("modal-product"));
    elements.push(document.getElementById("modal-factory"));
    elements.push(document.getElementById("cover-mask"));

    for (let el of elements) {
      el.classList.remove("modal-visible");
      el.classList.add("modal-hidden");
    }
    this.newCategory = {};
    this.newProduct = {};
  }


  public createOrUpdateCategory(): void {
    let result: Observable<any>;
    if (this.newCategory.id) {
      result = this.categoriesService.updateCategory(this.newCategory);
    } else {
      result = this.categoriesService.createCategory(this.newCategory);
    }
    result.subscribe(() => {
      toastr.success(this.SAVED_SUCCESSFULLY);
      this.loadCategories();
    }, () => toastr.error(this.SAVING_ERROR));
    this.newCategory = {};
    this.closeModal();
  }

  public updateCategoryOrderNumber(catId: number, orderNumber: number) {
    this.categoriesService.updateCategoryOrderNumber(catId, orderNumber).subscribe(
      () => {},
      () => toastr.error(this.SAVING_ERROR),
      () => toastr.success(this.SAVED_SUCCESSFULLY));
  }

  public addCategoryImageFile() {
    let file  = (<HTMLInputElement>document.getElementById("categoryImageUploader")).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      if (this.newCategory.image) {
        this.newCategory.image.categoryImageLink = null;
      }
      this.newCategory.imageInput = reader.result;
      this.newCategory.imageChanged = true;
    });
  }

  public addProductMainImage() {
    let file  = (<HTMLInputElement>document.getElementById("productMainImageUploader")).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      this.newProduct.imageInput = reader.result;
    });
  }

  public addProductImages() {
    let files  = (<HTMLInputElement>document.getElementById("productImagesUploader")).files;
    for (let i = 0; i < files.length; i++) {
      let reader = new FileReader();
      reader.readAsDataURL(files.item(i));
      reader.addEventListener('load', (event: any) => {
        if (!this.newProduct.imagesInput) {
          this.newProduct.imagesInput = [];
        }
        this.newProduct.imagesInput.push(reader.result);
      });
    }
  }

  public addImageForSaleOffer(offer: SaleOfferEntry): void {
    let index = this.newProduct.saleOffers.indexOf(offer);
    let uploaderId = "saleOfferImagesUploader_" + index;
    let file  = (<HTMLInputElement>document.getElementById(uploaderId)).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      offer.imageInput = reader.result;
    });
  }

  public removeCategory(categoryId: number): void {
    if (confirm("Вы уверены, что хотите удалить категорию?")) {
      this.categoriesService.removeCategory(categoryId).subscribe(
        res => {
          this.closeModal();
          let cat = this.itemsInList.find(x => x.id == categoryId);
          if (cat) {
            let index = this.itemsInList.indexOf(cat, 0);
            this.itemsInList.splice(index, 1);
          }
          toastr.success(this.REMOVED_SUCCESSFULLY);
        },
        err => {
          toastr.error(this.REMOVING_ERROR);
        });
    }
  }

  public createOrUpdateProduct(): void {
    console.log(this.newProduct);
    let result: Observable<any>;
    if (this.newProduct.id) {
      result = this.productsService.updateProduct(this.newProduct);
    } else {
      result = this.productsService.createProduct(this.newProduct);
    }
    result.subscribe(() => {
      this.loadProductsForActiveCategory();
      this.newProduct = {};
      this.closeModal();
      toastr.success(this.SAVED_SUCCESSFULLY);
    }, () => toastr.error(this.SAVING_ERROR));

  }

  public changeView(viewName: string, event: any): void {
    event.preventDefault();
    this.view = viewName;
  }

  public createNewOption(): void {
    let newOption: OfferOptionEntry = new OfferOptionEntry();
    newOption.values = [];
    newOption.editMode = true;
    this.offerOptions.splice(0, 0, newOption);
  }

  public addNewOptionValue(event: any): void {
    event.preventDefault();
    let newValue: OptionValueEntry = {};
    if (!this.editingOption.values) {
      this.editingOption.values = [];
    }
    this.editingOption.values.push(newValue);
  }

  public removeOptionValueFromOption(event: any, value: OptionValueEntry): void {
    event.preventDefault();
    let index = this.editingOption.values.indexOf(value);
    if (index && index != -1) {
      this.editingOption.values.splice(index, 1);
    }
  }

  public changeSelectedOption(): void {
    let option: OfferOptionEntry = this.offerOptions.find(x => x.id == this.newProductSelectedOptionId);
    if (option) {
      this.newProductSelectedOption = option;
    }
  }

  public addOptionToProduct(event: any) {
    event.preventDefault();
    let option: OfferOptionEntry = new OfferOptionEntry();
    let value: OptionValueEntry = this.newProductSelectedOption.values.find(x => x.value == this.newProductSelectedOptionValue);
    option.id = this.newProductSelectedOption.id;
    option.title = this.newProductSelectedOption.title;
    if (value) {
      option.selectedValue = value;
    } else {
      value = new OptionValueEntry();
      if (this.newProductSelectedOptionValue) {
        value.value = this.newProductSelectedOptionValue;
      } else {
        value.value = "";
      }
      value.editMode = true;
    }
    option.selectedValue = value;
    if (!this.newProduct.options) {
      this.newProduct.options = [];
    }
    this.newProduct.options.push(option);
    this.newProductSelectedOption = {};
    this.newProductSelectedOptionValue = "";
  }

  public removeOptionFromProduct(event: any, option: OfferOptionEntry) {
    event.preventDefault();
    this.newProduct.options = this.newProduct.options.filter(x => x != option);

  }

  public changeCategoryForProduct(): void {
    let cat = this.categories.find(x => x.id == this.newProductCategoryId);
    if (cat) {
      this.newProduct.category = cat;
    }
  }

  public editOption(option: OfferOptionEntry) {
    for (let opt of this.offerOptions) {
      opt.editMode = false;
    }
    option.editMode = true;
    this.editingOption = option;
  }

  public cancelEditing(option: OfferOptionEntry) {
    option.editMode = false;
    this.editingOption = {};
  }

  public createOrUpdateOption(option: OfferOptionEntry) {
    let observable: Observable<any>;
    if (this.editingOption.id) {
      observable = this.optionsService.updateOption(this.editingOption);
    } else {
      observable = this.optionsService.createOption(this.editingOption);
    }
    observable.subscribe(result => {
      option.id = result.id;
      option.title = result.title;
      option.name = result.name;
      option.values = result.values;
      option.editMode = false;
      this.loadProductOptions();
      this.editingOption = {};
      toastr.success(this.SAVED_SUCCESSFULLY)
    }, () => toastr.error(this.SAVING_ERROR));
  }

  public removeOption(option: OfferOptionEntry): void {
    if (confirm("Вы уверены, что хотите удалить характеристику?")) {
      this.optionsService.removeOption(option.id).subscribe(() => {
        // let index = this.offerOptions.indexOf(option);
        // if (index) {
        //   this.offerOptions.splice(index, 1);
        // }
        this.offerOptions = this.offerOptions.filter(o => o.id != option.id);
        toastr.success(this.REMOVED_SUCCESSFULLY);
      }, () => toastr.error(this.REMOVING_ERROR));
    }
  }

  public removeValueFromOption(value: OptionValueEntry): void {
    // let index = this.editingOption.values.indexOf(value);
    // if (index) {
    //   this.editingOption.values.splice(index, 1);
    // }
    this.editingOption.values = this.editingOption.values.filter(v => v != value);
  }

  public addOptionValueImage(value: OptionValueEntry): void {
    let uploaderId: string = "optionValueImageUploader_" + value.id;
    let file  = (<HTMLInputElement>document.getElementById(uploaderId)).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      value.imageInput = reader.result;
    });
  }

  public removeProduct(productId: number): void {
    if (confirm("Вы уверены, что хотите удалить товар?")) {
      this.productsService.removeProduct(productId).subscribe(() => {
        this.loadProductsForActiveCategory();
        this.newProduct = {};
        this.closeModal();
        toastr.success(this.REMOVED_SUCCESSFULLY);
      }, () => toastr.error(this.REMOVING_ERROR));
    }
  }

  public isSaleOffersInEditingProductPresent(): boolean {
    if (this.newProduct.saleOffers && this.newProduct.saleOffers.length > 0) {
      return true;
    }
    return false;
  }

  public addSaleOfferToEditingProduct(event: any): void {
    event.preventDefault();
    let saleOffer: SaleOfferEntry = new SaleOfferEntry();
    if (!this.newProduct.saleOffers) {
      this.newProduct.saleOffers = [];
    }
    if (!this.newProductSaleOfferOption) {
      this.newProductSaleOffersOptionId = this.offerOptions[0].id;
      this.newProductSaleOfferOption = this.offerOptions[0];
    }
    saleOffer.offerOption = this.newProductSaleOfferOption;
    this.newProduct.saleOffers.push(saleOffer);
  }

  public removeSaleOfferFromEditingProduct(event: any, offer:SaleOfferEntry): void {
    event.preventDefault();
    if (!offer.id) {
      this.newProduct.saleOffers = this.newProduct.saleOffers.filter(x => x !== offer);
      return;
    }
    this.productsService.removeSaleOffer(offer.id).subscribe(() => {
      let index = this.newProduct.saleOffers.indexOf(offer);
      if (index != -1) {
        this.newProduct.saleOffers.splice(index, 1);
      }
    });
  }

  public changeOptionOfSaleOffersOfEditingProduct(): void {
    let option: OfferOptionEntry = this.offerOptions.find(x => x.id == this.newProductSaleOffersOptionId);
    if (option) {
      this.newProductSaleOfferOption = option;
      for (let offer of this.newProduct.saleOffers) {
        offer.offerOption = option;
      }
    }
  }

  public changeOptionValueOfSaleOffer(offer: SaleOfferEntry, option: OfferOptionEntry): void {
    let optionValue = this.newProductSaleOfferOption.values.find(x => x.id == offer.optionValueId);
    if (optionValue) {
      offer.optionValue = optionValue;
    }
  }

  public loadFactories(): void {
    this.factoriesService.getFactories().subscribe(result => {
      this.factories = result;
    });
  }

  public createOrUpdateFactory(): void {
    if (this.newFactory.id) {
      this.factoriesService.updateFactory(this.newFactory).subscribe(
        () => {toastr.success(this.SAVED_SUCCESSFULLY)},
        () => {toastr.error(this.SAVING_ERROR)},
        () => {
          this.loadFactories();
          this.closeModal();
          this.newFactory = {};
        });
    } else {
      this.factoriesService.createFactory(this.newFactory).subscribe(
        () => {toastr.success(this.SAVED_SUCCESSFULLY)}, () => {toastr.error(this.SAVING_ERROR)}, () => {
          this.loadFactories();
          this.closeModal();
          this.newFactory = {};
        });
    }
  }

  public removeFactory(factory: FactoryEntry): void {
    if (confirm("Вы уверены, что хотите удалить производителя?")) {
      if (factory.id) {
        this.factoriesService.removeFactory(factory.id).subscribe(
          () => {
            toastr.success(this.REMOVED_SUCCESSFULLY);
          },
          () => {
            toastr.error(this.REMOVING_ERROR);
          },
          () => {
            this.newFactory = {};
            this.loadFactories();
            this.closeModal();
          });
      }
    }
  }

  public logout(event: any): void {
    event.preventDefault();
    this.authenticationService.logout();
  }

  public openModalFactory(event: any, factory: FactoryEntry): void {
    event.preventDefault();
    if (factory) {
      this.newFactory.id = factory.id;
      this.newFactory.title = factory.title;
      this.newFactory.description = factory.description;
      this.newFactory.popular = factory.popular;
    }
    this.openModal("modal-factory");
  }

  public loadPages(): void {
    this.pageService.getPages().subscribe(result => {
      this.pages = result;
      for (let page of this.pages) {
        if (!page.parentPageId) {
          this.topLevelPages.push(page);
        } else {
          let parentPage = this.pages.find(x => x.id == page.parentPageId);
          if (parentPage) {
            if (!parentPage.childPages) parentPage.childPages = [];
            parentPage.childPages.push(page);
          }
        }
      }
    });
  }

  public editPage(event: any, page: PageEntry): void {
    if (event) event.preventDefault();
    for (let p of this.pages) {
      if (p.id != page.id) p.active = false;
    }
    for (let p of this.topLevelPages) {
      if (p.id != page.id) p.active = false;
    }
    page.active = !page.active;
  }

  public addNewPage(): void {
    let newPage = new PageEntry();
    for (let p of this.pages) {
      p.active = false;
    }
    newPage.active = true;
    this.topLevelPages.splice(0, 0, newPage);
  }

  public removeImageFromProduct(event: any, img: ImageEntry): void {
    event.preventDefault();
    if (this.newProduct.mainImage == img) this.newProduct.mainImage = null;
    this.newProduct.images = this.newProduct.images.filter(x => x !== img);
    this.productsService.removeImageFromProduct(this.newProduct.id, img.id).subscribe(() => {});
  }

  public removePage(page: PageEntry, parentPage: PageEntry): void {
    if (confirm("Вы уверены, что хотите удалить страницу?")) {
      if (page.id) {
        this.pageService.removePage(page.id).subscribe(() => {
          toastr.success(this.REMOVED_SUCCESSFULLY);
          page.active = false;
        }, () => toastr.error(this.REMOVING_ERROR));
        if (!parentPage) {
          this.topLevelPages = this.topLevelPages.filter(p => p != page);
        } else {
          parentPage.childPages = parentPage.childPages.filter(p => p != page);
        }
      }
    }
  }

  public addPhotoGalleryItem() {
    let photoGalleryItem = new PhotoGalleryItemEntry();
    this.photoGalleryItems.push(photoGalleryItem);
  }

  public addImageToGalleryItem(item: PhotoGalleryItemEntry, index: number) {
    let files  = (<HTMLInputElement>document.getElementById("galleryItemImageUploader_" + index)).files;
    for (let i = 0; i < files.length; i++) {
      let reader = new FileReader();
      reader.readAsDataURL(files.item(i));
      reader.addEventListener('load', (event: any) => {
        this.photoGalleryItemService.addImageToGalleryItem(item.id, reader.result).subscribe(() => {
          this.loadPhotoGalleryItems();
        });
      });
    }
  }

  public addImageToFactory() {
    let file  = (<HTMLInputElement>document.getElementById("factoryImageUploader")).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      if (this.newFactory.image) {
        this.newFactory.image.originalImageLink = null;
      }
      this.newFactory.imageInput = reader.result;
    });
  }

  public uploadProducts() {
    let file  = (<HTMLInputElement>document.getElementById("productsFileUploader")).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      this.productsService.uploadProductsFile(reader.result).subscribe(() => {});
    });
  }

  public saveProductButtonDisabled(): boolean {
    return !this.newProduct.factoryId
      || !this.newProduct.title
      || !this.newProduct.categoryId
      || !this.newProduct.priceType;
  }

  public updateValueOrderNumber(valueId: number, orderNumber: number) {
    this.optionsService.updateOptionValueOrderNumber(valueId, orderNumber).subscribe(
      () => {toastr.success(this.SAVED_SUCCESSFULLY);},
      () => {toastr.error(this.SAVING_ERROR);})
  }
}
