import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FirstComponentComponent} from "./first-component/first-component.component";
import {SecondComponentComponent} from "./second-component/second-component.component";
import {LoginComponent} from "./login/login.component";
import {AuthGuard} from "./_guards/auth.guard";
import {MainPageComponent} from "./main-page/main-page.component";

const routes: Routes = [
  {path: '', component: MainPageComponent },
  {path: 'secondPage', component: SecondComponentComponent, canActivate: [AuthGuard] },
  {path: 'login', component: LoginComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
