import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Request} from "../dto/Request";

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss']
})
export class ManageProductComponent implements OnInit, OnDestroy{
  selectedIds: Request[] = [];
  cols: any[];
  isManual: boolean = true;

  constructor() {
    if (localStorage.getItem("SendArray")) {
      // @ts-ignore
      this.selectedIds = JSON.parse(localStorage.getItem("SendArray"));
      this.countPriority();

    }
  }

  ngOnInit(): void {
    console.log(this.selectedIds);
  }
  countPriority() {
    this.selectedIds.forEach((e, idx)=> e.priority = idx + 1)
  }

  ngOnDestroy(): void {
    localStorage.removeItem("SendArray");

  }


  onDropChange() {
    this.countPriority();

    console.log(this.selectedIds)
  }

  countAutomaticxOrder() {
    this.isManual = false;
  }

  test(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    console.log('ssss')
    return null;
  }
}
