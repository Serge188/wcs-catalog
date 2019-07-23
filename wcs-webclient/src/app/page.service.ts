import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";
import {OfferOptionEntry} from "./_models/offer-option-entry";
import {PageEntry} from "./_models/page-entry";

@Injectable({
  providedIn: 'root'
})
export class PageService {
  private apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public getPages(): Observable<any> {
    return this.http.get(this.apiUrl + `pages`).pipe();
  }

  public getMainMenuPages(): Observable<any> {
    return this.http.get(this.apiUrl + `pages/mainMenuPages`).pipe();
  }

  public getPageByAlias(alias: string): Observable<any> {
    return this.http.get(this.apiUrl + `pages/byAlias/${alias}`).pipe();
  }

  public createPage(page: PageEntry): Observable<any> {
    return this.http.post(this.apiUrl + 'pages', page).pipe();
  }

  public updatePage(page: PageEntry): Observable<any> {
    console.log(page);
    return this.http.put(this.apiUrl + 'pages', page).pipe();
  }

  public removePage(pageId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `pages/${pageId}`).pipe();
  }
}
