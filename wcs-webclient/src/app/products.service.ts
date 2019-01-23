import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {ProductEntry} from "./_models/product-entry";

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) {}

  public getPopularProducts(): Observable<any> {
    return this.http.get('http://localhost:8080/api/products/popular').pipe();
  }

  public getProductByAlias(alias: string): Observable<ProductEntry> {
    return this.http.get(`http://localhost:8080/api/products/byAlias/${alias}`).pipe();
  }

  public getCategoryProducts(categoryId: number): Observable<any> {
    return this.http.get(`http://localhost:8080/api/products/byCategory/${categoryId}`).pipe();
  }

  public getOneLevelCategoryProducts(categoryId: number): Observable<any> {
    return this.http.get(`http://localhost:8080/api/products/byCategoryForOneLevel/${categoryId}`).pipe();
  }
}
