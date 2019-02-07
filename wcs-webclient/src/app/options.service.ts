import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {OfferOptionEntry} from "./_models/offer-option-entry";

@Injectable({
  providedIn: 'root'
})
export class OptionsService {
  private apiUrl: string = `http://localhost:8080/`;

  constructor(private http: HttpClient) {}

  public getProductOptions(): Observable<any> {
    return this.http.get(this.apiUrl + 'api/options').pipe();
  }

  public createOption(option: OfferOptionEntry): Observable<any> {
    return this.http.post(this.apiUrl + 'api/options/option', option).pipe();
  }

  public updateOption(option: OfferOptionEntry): Observable<any> {
    return this.http.put(this.apiUrl + 'api/options/option', option).pipe();
  }

  public removeOption(optionId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `api/options/option/${optionId}`).pipe();
  }
}
