import {Component, OnInit} from '@angular/core';
import {Request} from "../../dto/Request";
import {RequestService} from "../../services/request.service";
import {RequestPosition} from "../../dto/RequestPosition";
import {MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {formatDate} from "@angular/common";
import {UserService} from "../../services/user.service";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.scss'],
  providers: [MessageService]
})
export class RequestComponent implements OnInit {

  request: Request[] = [];
  selectedRequests: Request[] = [];

  userRole: string = '';

  requestPositions: RequestPosition[] = [];

  isProductionPlanMode = false;

  constructor(public requestService: RequestService, public router: Router, public authService: AuthService) {
    this.userRole = authService.userRole;
  }

  async ngOnInit() {
    this.request = await this.requestService.getRequests();
  }

  async checkRequest(selected: any) {
    if (selected.length == 1) {
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


  sendRequestsPositions() {
    this.selectedRequests.forEach(req => req.requestId = req.id);
    localStorage.setItem('SendArray', JSON.stringify(this.selectedRequests));
    this.router.navigate(['/manage']);
  }


  formatDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }

  async refreshDitail() {

    this.requestPositions = await this.requestService.getRequestPositionById(this.selectedRequests[0].id);
  }

  async refreshMain() {
    this.request = await this.requestService.getRequests();
  }
}
