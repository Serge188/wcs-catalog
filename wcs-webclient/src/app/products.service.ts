import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {ProductEntry} from "./_models/product-entry";
import {CategoryEntry} from "./_models/category-entry";
import { environment } from '../environments/environment';
import {CategoryFilter} from "./_models/category-filter";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  // private apiUrl: string = `http://178.62.212.25:8080/`;
  private apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

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
}
