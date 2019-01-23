import {Component, Injectable} from '@angular/core';
import {Inject} from "@angular/compiler/src/core";
import { HttpClient } from '@angular/common/http';
import {User} from "./_models/User";
import {Router} from "@angular/router";
import {AuthenticationService} from "./authentication.service";
import {CategoriesService} from "./categories.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

@Injectable()
export class AppComponent {
  currentUser: User;

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private categoriesService: CategoriesService) {

    this.authenticationService.currentUser.subscribe(x => this.currentUser = x);
  }

  logout() {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
}
