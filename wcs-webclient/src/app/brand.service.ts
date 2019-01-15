import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";

@Injectable({
  providedIn: 'root'
})
export class BrandService {

  constructor(private http: HttpClient) { }

  public getPopularBrands(): Observable<any> {
    return this.http.get('http://localhost:8080/api/brands/popular').pipe();
  }
}
