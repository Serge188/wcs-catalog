import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BrandService {

  // private apiUrl: string = `http://178.62.212.25:8080/`;
  private apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public getPopularBrands(): Observable<any> {
    return this.http.get(this.apiUrl + 'brands/popular').pipe();
  }
}
