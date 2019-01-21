import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CategoryEntry} from "./_models/category-entry";
import {Observable} from "rxjs/internal/Observable";

@Injectable({
  providedIn: 'root'
})
export class CategoriesService {
  public isPageAdminPanel = false;

  constructor(private http: HttpClient) {}

  public getCategories(): Observable<any> {
    return this.http.get('http://localhost:8080/api/categories/sideMenuCategories').pipe();
  }

  public getCategoryByAlias(alias: string): Observable<any> {
    return this.http.get(`http://localhost:8080/api/categories/byAlias/${alias}`).pipe();
  }
}
