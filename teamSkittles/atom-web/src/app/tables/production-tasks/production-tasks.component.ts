import {Component, OnInit} from '@angular/core';
import {formatDate} from "@angular/common";
import {Request} from "../../dto/Request";
import {RequestService} from "../../services/request.service";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-production-tasks',
  templateUrl: './production-tasks.component.html',
  styleUrls: ['./production-tasks.component.scss'],
  providers: [MessageService]
})
export class ProductionTasksComponent implements OnInit {

  requests: Request[] = [];
  selectedRequests: Request[] = [];
  display: any;

  constructor(public requestService: RequestService) {
  }
  async ngOnInit() {
    this.requests = await this.requestService.getRequests();
  }

  test() {
    this.display = true;
    console.log(this.selectedRequests)
  }
  formatDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }

  isEnabled(request: Request) {
    return (request.state?.code === 'DRAFT' || request.state?.code === 'BLANK' || request.state == null)
  }

}

