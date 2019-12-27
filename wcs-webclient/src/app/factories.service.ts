import { Injectable } from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {FactoryEntry} from "./_models/factory-entry";

@Injectable({
  providedIn: 'root'
})
export class FactoriesService {

  private apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public getFactories(): Observable<any> {
    return this.http.get(this.apiUrl + `brands`).pipe();
  }

  public getPopularBrands(): Observable<any> {
    return this.http.get(this.apiUrl + 'brands/popular').pipe();
  }

  public createFactory(factory: FactoryEntry): Observable<any> {
    return this.http.post(this.apiUrl + `brands`, factory).pipe();
  }

  public updateFactory(factory: FactoryEntry): Observable<any> {
    return this.http.put(this.apiUrl + `brands`, factory).pipe();
  }

  public removeFactory(id: number): Observable<any> {
    return this.http.delete(this.apiUrl + `brands/${id}`).pipe();
  }

  public getBrandByAlias(alias: string) {
    return this.http.get(this.apiUrl + `brands/byAlias/${alias}`).pipe();
  }
}
