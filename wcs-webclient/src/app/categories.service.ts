import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CategoryEntry} from "./_models/category-entry";
import {Observable} from "rxjs/internal/Observable";

@Injectable({
  providedIn: 'root'
})
export class CategoriesService {

  constructor(private http: HttpClient) {}

  public getCategories(): Observable<any> {
    return this.http.get(`http://localhost:8080/api/categories/sideMenuCategories`).pipe();
  }

  public getCategoryByAlias(alias: string): Observable<any> {
    return this.http.get(`http://localhost:8080/api/categories/byAlias/${alias}`).pipe();
  }

  public updateCategory(category: CategoryEntry): Observable<any> {
    return this.http.put(`http://localhost:8080/api/categories`, category).pipe();
  }

  public createCategory(category: CategoryEntry): Observable<any> {
    return this.http.post(`http://localhost:8080/api/categories/new`, category).pipe();
  }

  public removeCategory(categoryId: number): Observable<any> {
    return this.http.delete(`http://localhost:8080/api/categories/${categoryId}`).pipe();
  }
}
