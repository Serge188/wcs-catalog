import {Component, Input, OnInit} from '@angular/core';
import {CategoryEntry} from "../_models/category-entry";

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.css']
})
export class SideMenuComponent implements OnInit {

  @Input() categories: CategoryEntry[];

  constructor() { }

  ngOnInit() {
  }

}
