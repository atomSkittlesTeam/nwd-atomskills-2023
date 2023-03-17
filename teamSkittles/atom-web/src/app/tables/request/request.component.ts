import {Component, OnInit} from '@angular/core';
import {Request} from "../../dto/Request";
import {RequestService} from "../../services/request.service";
import {RequestPosition} from "../../dto/RequestPosition";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.scss'],
  providers: [MessageService]
})
export class RequestComponent implements OnInit {

  request: Request[] = [];
  selectedRequest: Request;

  requestPosition: RequestPosition[] = [];

  constructor(public requestService: RequestService,) {

  }

  async ngOnInit() {
    this.request = await this.requestService.getRequests();
    console.log(this.request)
  }

  async test(id: number) {

    console.log(await this.requestService.getRequestPositionById(id));
    this.requestPosition = await this.requestService.getRequestPositionById(id);
  }

}
