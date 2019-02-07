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
  public offerOptions: OfferOptionEntry[];
  public newProductSelectedOption: OfferOptionEntry = {};
  public newProductSelectedOptionId: number;
  public newProductSelectedOptionValue: string = "";
  public newProductCategoryId: number;
  public editingOption: OfferOptionEntry = {};

  constructor(private categoriesService: CategoriesService,
              private productsService: ProductsService, private optionsService: OptionsService) { }

  ngOnInit() {
    this.view = "categories";
    this.loadCategories();
    this.loadProductOptions();
  }

  public loadCategories(): void {
    this.itemsInList = [];
    this.topLevelCategories = [];
    this.categoriesService.getCategories().subscribe(result => {
      console.log(result);
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
      this.productsService.getOneLevelCategoryProducts(this.activeCategory.id).subscribe(result => {
        this.activeCategory.products = result;
        console.log(result);
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

  public loadProductOptions(): void {
    this.optionsService.getProductOptions().subscribe(result => {
      this.offerOptions = result;
      console.log(this.offerOptions);
    })
  }

  public removeProductsFromList(category: CategoryEntry): void {
    console.log(category);
    for (let p of category.products) {
      console.log(p);
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
    for (let cat of this.categories) {
      if (cat.parentCategoryId == category.id) {
        let index = this.itemsInList.indexOf(category, 0);
        if (index != -1) {
          this.itemsInList.splice(index + 1, 0, cat);
          cat.level = category.level + 1;
          cat.expanded = false;
          cat.selected = false;
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

  public getPadding(category: CategoryEntry): any {
      let padding: any = {};
      if (category.level == 1) {
        padding['font-weight'] = 'bold';
      }
      if (category.level > 1) {
        let value = (category.level - 1) * 45;
        padding['padding-left'] = value + 'px';
      }
      return padding;
  }

  public openModalCategory(event: any, item: any, createChild: boolean): void {
    event.preventDefault();
    // let mask: HTMLElement = document.getElementById("cover-mask");
    // let form: HTMLElement = document.getElementById("modal-category");
    // mask.classList.add("modal-visible");
    // mask.classList.remove("modal-hidden");
    // form.classList.add("modal-visible");
    // form.classList.remove("modal-hidden");
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

  public openModalProduct(event: any, product: ProductEntry, category: CategoryEntry) {
    this.openModal("modal-product");
    if (product) {
      this.newProduct.id = product.id;
      this.newProduct.categoryId = product.category.id;
      this.newProduct.category = product.category;
      this.newProduct.price = product.price;
      this.newProduct.discountPrice = product.discountPrice;
      this.newProduct.title = product.title;
      this.newProduct.description = product.description;
      this.newProduct.mainImage = product.mainImage;
      this.newProduct.images = product.images;
    }
    if (category) {
      this.newProduct.category = category;
    }
    console.log(this.newProduct);
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
      this.loadCategories();
    });
    this.newCategory = {};
    this.closeModal();
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

  }

  public removeCategory(categoryId: number): void {
    this.categoriesService.removeCategory(categoryId).subscribe(
      res => {
      this.closeModal();
      let cat = this.itemsInList.find(x => x.id == categoryId);
      if (cat) {
        let index = this.itemsInList.indexOf(cat, 0);
        this.itemsInList.splice(index, 1);
      }
    },
        err => {
        alert("При удалении категории произошла оишбка: " + err);
      });
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
      console.log(this.newProductSelectedOption);
    }
  }

  public addOptionToProduct(event: any) {
    event.preventDefault();
    let option: OfferOptionEntry = new OfferOptionEntry();
    let value: OptionValueEntry = this.newProductSelectedOption.values.find(x => x.value == this.newProductSelectedOptionValue);
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
    console.log(option);
    if (!this.newProduct.options) {
      this.newProduct.options = [];
    }
    this.newProduct.options.push(option);
    this.newProductSelectedOption = {};
    this.newProductSelectedOptionValue = "";
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
    console.log(this.editingOption);
  }

  public cancelEditing(option: OfferOptionEntry) {
    option.editMode = false;
    this.editingOption = {};
  }

  public createOrUpdateOption(option: OfferOptionEntry) {
    let observable: Observable<any>;
    console.log(option);
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
    });
  }

  public removeOption(option: OfferOptionEntry): void {
    this.optionsService.removeOption(option.id).subscribe(()=>{
      let index = this.offerOptions.indexOf(option);
      if (index) {
        this.offerOptions.splice(index, 1);
      }
    });
  }

  public removeValueFromOption(option: OfferOptionEntry, value: OptionValueEntry): void {
    let index = this.editingOption.values.indexOf(value);
    if (index) {
      this.editingOption.values.splice(index, 1);
    }
  }

  public addOptionValueImage(value: OptionValueEntry): void {
    let uploaderId: string = "optionValueImageUploader_" + value.id;
    let file  = (<HTMLInputElement>document.getElementById(uploaderId)).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      value.imageInput = reader.result;
      console.log(this.editingOption);
      // if (this.newCategory.image) {
      //   this.newCategory.image.categoryImageLink = null;
      // }
      // this.newCategory.imageInput = reader.result;
      // this.newCategory.imageChanged = true;
    });
  }

}
