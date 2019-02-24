import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {OfferOptionEntry} from "./_models/offer-option-entry";
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OptionsService {
  // private apiUrl: string = `http://localhost:8080/`;
  // private apiUrl: string = `http://178.62.212.25:8080/`;
  private apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getProductOptions(): Observable<any> {
    return this.http.get(this.apiUrl + 'options').pipe();
  }

  public createOption(option: OfferOptionEntry): Observable<any> {
    return this.http.post(this.apiUrl + 'options/option', option).pipe();
  }

  public updateOption(option: OfferOptionEntry): Observable<any> {
    return this.http.put(this.apiUrl + 'options/option', option).pipe();
  }

  public removeOption(optionId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `options/option/${optionId}`).pipe();
  }
}
