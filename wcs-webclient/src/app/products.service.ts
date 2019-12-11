import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {ProductEntry} from "./_models/product-entry";
import {CategoryEntry} from "./_models/category-entry";
import { environment } from '../environments/environment';
import {CategoryFilter} from "./_models/category-filter";
import {ProductSimplifiedEntry} from "./_models/product-simplified-entry";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private apiUrl: string = environment.apiUrl;
  public $itemFavoriteAdded: EventEmitter<ProductSimplifiedEntry>;

  constructor(private http: HttpClient) {
    this.$itemFavoriteAdded = new EventEmitter();
  }

  public getPopularProducts(): Observable<any> {
    return this.http.get(this.apiUrl + 'products/popular').pipe();
  }

  public getProductByAlias(alias: string): Observable<ProductEntry> {
    return this.http.get(this.apiUrl + `products/byAlias/${alias}`).pipe();
  }

  public getCategoryProducts(categoryId: number, filter: CategoryFilter): Observable<any> {
    return this.http.post(this.apiUrl + `products/byCategory/${categoryId}`, filter).pipe();
  }

  public getOneLevelCategoryProducts(categoryId: number): Observable<any> {
    return this.http.get(this.apiUrl + `products/byCategoryForOneLevel/${categoryId}`).pipe();
  }


  public updateProduct(product: ProductEntry): Observable<any> {
    return this.http.put(this.apiUrl + `products`, product).pipe();
  }

  public createProduct(product: ProductEntry): Observable<any> {
    return this.http.post(this.apiUrl + `products`, product).pipe();
  }

  public removeProduct(productId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `products/${productId}`).pipe();
  }

  public removeSaleOffer(saleOfferId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `products/saleOffer/${saleOfferId}`).pipe();
  }

  public removeImageFromProduct(productId, imageId): Observable<any> {
    return this.http.delete(this.apiUrl + `products/${productId}/images/${imageId}`).pipe()
  }

  public testProduct(): Observable<any> {
    return this.http.get(this.apiUrl + `products/testProduct`);
  }

  public uploadProductsFile(fileData: any): Observable<any> {
    return this.http.post(this.apiUrl + `products/upload`, fileData);
  }

  public loadSimplifiedProducts(productIds: number[]): Observable<any> {
    return this.http.post(this.apiUrl + `products/simplified`, productIds);
  }

  public getProductIdsFromLocalStorage(groupId: string): number[] {
    let resultIds = [];
    let targetItem = localStorage.getItem(groupId);
    if (targetItem) {
      resultIds = JSON.parse(targetItem);
    }
    return resultIds;
  }

  public removeProductIdFromLocalStorage(groupId: string, productId: number) {
    let ids = this.getProductIdsFromLocalStorage(groupId);
    localStorage.setItem(groupId, JSON.stringify(ids.filter(x => x != productId)));
  }

  public addProductIdToLocalStorage(groupId: string, productId: number) {
    let ids = this.getProductIdsFromLocalStorage(groupId);
    localStorage.setItem(groupId, JSON.stringify(ids.push(productId)));
  }

  public emitFavoriteAdded(product: ProductSimplifiedEntry) {
    this.$itemFavoriteAdded.emit(product);
  }
}
