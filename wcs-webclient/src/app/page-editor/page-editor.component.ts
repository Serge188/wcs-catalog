import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {PageEntry} from "../_models/page-entry";
import {PageService} from "../page.service";

@Component({
  selector: 'app-page-editor',
  templateUrl: './page-editor.component.html',
  styleUrls: ['../admin-panel/admin-panel.component.css']
})
export class PageEditorComponent implements OnInit {

  @Input() parentPages: PageEntry[];
  @Input() page: PageEntry;

  public possibleParentPages: PageEntry[] = [];

  constructor(private pageService: PageService) { }

  ngOnInit() {
    for (let page of this.parentPages) {
      if (page.id != this.page.id) this.possibleParentPages.push(page);
    }
  }

  public addPageImageFile() {
    // this.addImageFile("pageImageUploader", this.page.imageInput);
    let file  = (<HTMLInputElement>document.getElementById("pageImageUploader")).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      this.page.imageInput = reader.result;
    });
    if(this.page.image) {
      this.page.image.categoryImageLink = null;
    }
  }

  public addPageSliderImageFile() {
    // this.addImageFile("pageSliderImageUploader", this.page.sliderImageInput);
    let file  = (<HTMLInputElement>document.getElementById("pageSliderImageUploader")).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      this.page.sliderImageInput = reader.result;
    });
    if (this.page.sliderImage) {
      this.page.sliderImage.previewImageLink = null;
    }
  }

  private addImageFile(uploaderId: string, input: any) {
    let file  = (<HTMLInputElement>document.getElementById(uploaderId)).files.item(0);
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.addEventListener('load', (event: any) => {
      input = reader.result;
    });
  }

  public createPage(): void {
    if (this.page.id){
      this.pageService.updatePage(this.page).subscribe(() => this.page.active = false);
    } else {
      this.pageService.createPage(this.page).subscribe(() => this.page.active = false);
    }
  }

  public removePage(): void {
    if (this.page.id) {
      this.pageService.removePage(this.page.id).subscribe(() => {
        this.page.active = false;
      });
    }
  }
}
