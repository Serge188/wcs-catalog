import { Component, OnInit } from '@angular/core';
import {PageService} from "../page.service";
import {ActivatedRoute} from "@angular/router";
import {PageEntry} from "../_models/page-entry";

@Component({
  selector: 'app-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent implements OnInit {
  public pageAlias: string;
  public page: PageEntry;

  constructor(private pageService: PageService,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.pageAlias = this.route.snapshot.paramMap.get('pageAlias');
    this.loadPageByAlias();
  }

  public loadPageByAlias(): void {
    this.pageService.getPageByAlias(this.pageAlias).subscribe((result) => {
      this.page = result;
    });
  }

  public getShortContent(content: string): string {
    return content.substr(0, 1200) + "...";
  }
}
