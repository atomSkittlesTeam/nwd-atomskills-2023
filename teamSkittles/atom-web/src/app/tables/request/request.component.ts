import {Component, OnInit} from '@angular/core';
import {Request} from "../../dto/Request";
import {RequestService} from "../../services/request.service";
import {RequestPosition} from "../../dto/RequestPosition";
import {MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {formatDate} from "@angular/common";

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.scss'],
  providers: [MessageService]
})
export class RequestComponent implements OnInit {

  request: Request[] = [];
  selectedRequests: Request[] = [];

  requestPositions: RequestPosition[] = [];

  isProductionPlanMode = false;

  constructor(public requestService: RequestService, public router: Router) {

  }

  async ngOnInit() {
    this.request = await this.requestService.getRequests();
    console.log(this.request)
  }

  async test(selected: any) {
    if (selected.length == 1) {
      console.log(await this.requestService.getRequestPositionById(selected[0].id));
      this.requestPositions = await this.requestService.getRequestPositionById(selected[0].id);
    } else {
      this.requestPositions = [];
    }
  }

  onProductionPlanModeChange() {
    this.isProductionPlanMode = !this.isProductionPlanMode;
    if (!this.isProductionPlanMode) {
      this.selectedRequests = [];
    }
  }

  test2() {
    console.log('heyy')
  }

  sendRequestsPositions() {
    localStorage.setItem('SendArray', JSON.stringify(this.selectedRequests));
    this.router.navigate(['/manage']);
  }

  getReleaseDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }
}
