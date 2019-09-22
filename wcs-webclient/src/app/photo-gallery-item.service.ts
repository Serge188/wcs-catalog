import { Injectable } from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PhotoGalleryItemEntry} from "./_models/photo-gallery-item-entry";

@Injectable({
  providedIn: 'root'
})
export class PhotoGalleryItemService {

  private apiUrl: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getPhotoGalleryItems(): Observable<any> {
    return this.http.get(this.apiUrl + 'photoGalleryItems');
  }

  public createOrUpdateGalleryItem(item: PhotoGalleryItemEntry): Observable<any> {
    return this.http.post(this.apiUrl + 'photoGalleryItems', item);
  }

  public deleteGalleryItem(itemId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `photoGalleryItems/${itemId}`);
  }

  public addImageToGalleryItem(itemId: number, imageInput: any): Observable<any> {
    return this.http.put(this.apiUrl + `photoGalleryItems/${itemId}/image`, imageInput)
  }

  public deleteImageFromGalleryItem(imageId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `photoGalleryItems/image/${imageId}`)
  }
}
