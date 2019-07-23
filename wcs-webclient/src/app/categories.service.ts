import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CategoryEntry} from "./_models/category-entry";
import {Observable} from "rxjs/internal/Observable";
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoriesService {

  // private apiUrl: string = `http://178.62.212.25:8080/`;
  private apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getCategories(): Observable<any> {
    return this.http.get(this.apiUrl + `categories/sideMenuCategories`).pipe();
  }

  public getCategoriesWithProductsCount(): Observable<any> {
    return this.http.get(this.apiUrl + `categories/sideMenuCategoriesWithProductCount`).pipe();
  }

  public getCategoryByAlias(alias: string): Observable<any> {
    return this.http.get(this.apiUrl + `categories/byAlias/${alias}`).pipe();
  }

  public updateCategory(category: CategoryEntry): Observable<any> {
    return this.http.put(this.apiUrl + `categories`, category).pipe();
  }

  public createCategory(category: CategoryEntry): Observable<any> {
    return this.http.post(this.apiUrl + `categories/new`, category).pipe();
  }

  public removeCategory(categoryId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `categories/${categoryId}`).pipe();
  }

  public getPosssibleFilterOptions(categoryId: number): Observable<any> {
    return this.http.get(this.apiUrl + `categories/possibleFilterOptions/${categoryId}`).pipe();
  }

  public getPricesRange(categoryId: number): Observable<any> {
    return this.http.get(this.apiUrl + `categories/pricesRange/${categoryId}`).pipe();
  }

  public getFactoriesInCategory(categoryId: number): Observable<any> {
    return this.http.get(this.apiUrl + `categories/factories/${categoryId}`).pipe();
  }

  public getTopLevelCategories(): Observable<any> {
    return this.http.get(this.apiUrl + `categories/topLevelCategories`);
  }
}
