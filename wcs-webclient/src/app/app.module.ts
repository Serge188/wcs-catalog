import { BrowserModule } from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { FirstComponentComponent } from './first-component/first-component.component';
import { SecondComponentComponent } from './second-component/second-component.component';
import { LoginComponent } from './login/login.component';
import { AlertComponent } from './alert/alert.component';
import {ErrorInterceptor} from "./_helpers/error.interceptor";
import {JwtInterceptor} from "./_helpers/jwt.interceptor";
import {FormsModule} from "@angular/forms";
import { MainPageComponent } from './main-page/main-page.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CarouselModule} from "ngx-owl-carousel-o";
import { AdminPanelComponent } from './admin-panel/admin-panel.component';
import { HeaderComponent } from './header/header.component';
import { SideMenuComponent } from './side-menu/side-menu.component';
import { ProductPageComponent } from './product-page/product-page.component';
import { FooterComponent } from './footer/footer.component';
import { ProductsListComponent } from './products-list/products-list.component';
import { CategoryPageComponent } from './category-page/category-page.component';
import { BreadcrumbsComponent } from './breadcrumbs/breadcrumbs.component';
import { PageComponent } from './page/page.component';
import { PageEditorComponent } from './page-editor/page-editor.component';
import { OwlModule } from 'ngx-owl-carousel';
import { CatalogComponent } from './catalog/catalog.component';
import { BottomPanelComponent } from './bottom-panel/bottom-panel.component';
import { BrandsComponent } from './brands/brands.component';
import { BrandPageComponent } from './brand-page/brand-page.component';
import { SearchComponent } from './search/search.component';

@NgModule({
  declarations: [
    AppComponent,
    FirstComponentComponent,
    SecondComponentComponent,
    LoginComponent,
    AlertComponent,
    MainPageComponent,
    AdminPanelComponent,
    HeaderComponent,
    SideMenuComponent,
    ProductPageComponent,
    FooterComponent,
    ProductsListComponent,
    CategoryPageComponent,
    BreadcrumbsComponent,
    PageComponent,
    PageEditorComponent,
    CatalogComponent,
    BottomPanelComponent,
    BrandsComponent,
    BrandPageComponent,
    SearchComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    CarouselModule,
    BrowserAnimationsModule,
    OwlModule
  ],
  schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
