import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ProductEntry} from "../_models/product-entry";
import {ProductsService} from "../products.service";
import {ProductSearchEntry} from "../_models/product-search-entry";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  private searchString: string;
  public products: ProductSearchEntry;

  constructor(private route: ActivatedRoute, private productsService: ProductsService) {}

  ngOnInit() {
    this.searchString = this.route.snapshot.paramMap.get('searchString');
    this.loadProductsBySearchString();
  }

  public loadProductsBySearchString() {
    console.log(this.searchString);
    this.productsService.getProductsBySearchString(this.searchString).subscribe(result => {
      this.products = result;
    });
  }

}
