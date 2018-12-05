import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-first-component',
  templateUrl: './first-component.component.html',
  styleUrls: ['./first-component.component.css']
})
export class FirstComponentComponent implements OnInit {

  public user: any;

  constructor(private http: HttpClient) { }

  ngOnInit() {
  }

  public getUsers(): void {
    this.http.get("http://localhost:8080").toPromise().then(result => {
      this.user = result;
      console.log(this.user);
    });
  }

}
