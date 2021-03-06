import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FirstComponentComponent} from "./first-component/first-component.component";
import {SecondComponentComponent} from "./second-component/second-component.component";
import {LoginComponent} from "./login/login.component";
import {AuthGuard} from "./_guards/auth.guard";
import {MainPageComponent} from "./main-page/main-page.component";
import {AdminPanelComponent} from "./admin-panel/admin-panel.component";
import {ProductPageComponent} from "./product-page/product-page.component";
import {CategoryPageComponent} from "./category-page/category-page.component";
import {PageComponent} from "./page/page.component";
import {CatalogComponent} from "./catalog/catalog.component";
import {CommonModule} from "@angular/common";
import {BrandsComponent} from "./brands/brands.component";
import {BrandPageComponent} from "./brand-page/brand-page.component";
import {SearchComponent} from "./search/search.component";

const routes: Routes = [
  {path: '', component: MainPageComponent },
  {path: 'secondPage', component: SecondComponentComponent, canActivate: [AuthGuard] },
  {path: 'login', component: LoginComponent },
  {path: 'manager', component: AdminPanelComponent, canActivate: [AuthGuard] },
  {path: 'product/:alias', component: ProductPageComponent },
  {path: 'catalog', component: CatalogComponent },
  {path: 'catalog/:categoryAlias', component: CategoryPageComponent },
  {path: 'catalog/:parentCategoryAlias/:categoryAlias', component: CategoryPageComponent },
  {path: 'page/:pageAlias', component: PageComponent },
  {path: 'brands', component: BrandsComponent },
  {path: 'brands/:brandAlias', component: BrandPageComponent },
  {path: 'search/:searchString', component: SearchComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
