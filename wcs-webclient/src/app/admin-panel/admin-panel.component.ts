import { Component, OnInit } from '@angular/core';
import {CategoriesService} from "../categories.service";

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  constructor(private categoriesService: CategoriesService) { }

  ngOnInit() {
    this.categoriesService.isPageAdminPanel = true;
  }

}
