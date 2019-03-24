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

  constructor(private pageService: PageService) { }

  ngOnInit() {
    this.pageService.getMainMenuPages().subscribe(result => {
      this.mainMenuPages = result;
    });
  }

  public openFavorites(event: any): void {
    event.preventDefault();
  }

  public openCart(event: any): void {
    event.preventDefault();
  }

}
