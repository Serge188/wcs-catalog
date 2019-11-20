import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import {AuthenticationService} from "../authentication.service";
import {AlertService} from "../alert.service";
import {User} from "../_models/User";
import * as toastr from 'toastr';


@Component({templateUrl: 'login.component.html'})
export class LoginComponent implements OnInit {
  loading = false;
  submitted = false;
  returnUrl: string;
  user: User = new User();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService,
    private alertService: AlertService
  ) {
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  submit() {
    this.submitted = true;

    this.loading = true;
    this.authenticationService.login(this.user.username, this.user.password)
      .toPromise()
      .then(result => {
        this.router.navigate([this.returnUrl]);
      }, () => {toastr.error("Неверный логин или пароль")}).finally(() => this.loading = false);
  }
}
