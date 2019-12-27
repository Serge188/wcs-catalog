import { Component, OnInit } from '@angular/core';
import {FactoriesService} from "../factories.service";
import {FactoryEntry} from "../_models/factory-entry";

@Component({
  selector: 'app-brands',
  templateUrl: './brands.component.html',
  styleUrls: ['./brands.component.css']
})
export class BrandsComponent implements OnInit {

  public factories: FactoryEntry[];

  constructor(private brandService: FactoriesService) { }

  ngOnInit() {
    this.brandService.getFactories().subscribe(result => {
      this.factories = result;
    });
  }

}
