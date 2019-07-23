import { Component, OnInit } from '@angular/core';
import {PageService} from "../page.service";
import {PageEntry} from "../_models/page-entry";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  public mainMenuPages: PageEntry[];

  public busketItemsCount: number;
  public busketItemsSum: number;

  constructor(private pageService: PageService) { }

  ngOnInit() {
    this.pageService.getMainMenuPages().subscribe(result => {
      this.mainMenuPages = result;
    });
    this.busketItemsCount = JSON.parse(localStorage.getItem("busketItemsCount"));
    if (this.busketItemsCount == null) this.busketItemsCount = 0;
    this.busketItemsSum = JSON.parse(localStorage.getItem("busketItemsSum"));
    if (this.busketItemsSum == null) this.busketItemsSum = 0;
  }

  public openFavorites(event: any): void {
    event.preventDefault();
  }

  public openCart(event: any): void {
    event.preventDefault();
  }

  public getFavoritesCount(): string {
    return localStorage.getItem("favorites") != null ? JSON.parse(localStorage.getItem("favorites")).length : 0;
  }

  public getBusketItemsCount(): number {
    return localStorage.getItem("busketItemsCount") != null ? JSON.parse(localStorage.getItem("busketItemsCount")) : 0;
  }

  public getItemsWord(): string {
    let itemsCount = this.getBusketItemsCount();
    if (itemsCount == null || itemsCount == 0) {
      return "";
    }
    if (itemsCount == 1 || itemsCount % 10 == 1) {
      return "товар";
    }
    if((itemsCount >= 2 && itemsCount <= 4) || (itemsCount % 10 >= 2 && itemsCount % 10 <= 4)) {
      return "товара";
    }
    return "товаров";
  }

  public getBusketItemsSum(): string {
    let sum = localStorage.getItem("busketItemsSum");
    if (sum != null) {
      return sum;
    }
    return "0";
  }

}
