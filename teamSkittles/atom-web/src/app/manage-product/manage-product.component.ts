import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Request} from "../dto/Request";
import {RequestService} from "../services/request.service";
import {MessageService} from "primeng/api";
import {StatusRequest} from "../dto/status-request";
import {formatDate} from "@angular/common";

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss'],
  providers: [MessageService]
})
export class ManageProductComponent implements OnInit, OnDestroy {
  selectedRequests: Request[] = [];
  cols: any[];
  isManual: boolean = true;
  StatusRequest = StatusRequest;

  constructor(public requestService: RequestService, public messageService: MessageService) {
    if (localStorage.getItem("SendArray")) {
      // @ts-ignore
      this.selectedRequests = JSON.parse(localStorage.getItem("SendArray"));
      console.log('1')
      localStorage.removeItem(("SendArray"))
    }
  }

  async ngOnInit() {
    console.log(this.selectedRequests, '2');
    const result = await this.requestService.getBlank();
    this.selectedRequests = result.concat(this.selectedRequests);
    this.countPriority();

  }

  // [ngClass]="{'broken': message.type === 'machineBroke'}"
  countPriority() {
    this.selectedRequests.forEach((e, idx) => e.priority = idx + 1)
  }

  isEnabled(request: Request) {
    return (request.state?.code === 'DRAFT' || request.state?.code === 'BLANK' || request.state == null)
  }

  ngOnDestroy(): void {
    localStorage.removeItem("SendArray");
  }

  onDropChange() {
    this.countPriority();
  }

  async countAutomaticsOrder() {
    this.isManual = false;
    await this.requestService.orderedPlan(this.selectedRequests
      .filter(req => req.state.code === "BLANK" || req.state.code === "DRAFT" || req.state.code === null)
      .map(e => e.requestId)).then(data => {
      this.selectedRequests = data;
    }).catch((data) => {
      this.messageService.add({
        severity: 'error',
        summary: 'Ошибка регистрации',
        detail: 'Произошла ошибка',
      })
    });
  }

  async savePlan() {
    console.log(this.selectedRequests, 'save');
    await this.requestService.savePlan(this.selectedRequests
      .filter(req => req.state.code === "BLANK" || req.state.code === "DRAFT" || req.state.code === null))
      .then(data => {
      this.selectedRequests = data;
    }).catch((data) => {
      this.messageService.add({
        severity: 'error',
        summary: 'Ошибка регистрации',
        detail: 'Произошла ошибка',
      })
    });
  }

  async approvePosition(id: number) {
    console.log(this.selectedRequests, 'approvePosition');

    await this.requestService.approvePosition(id, this.selectedRequests
      .filter(req => req.state.code === "BLANK" || req.state.code === "DRAFT" || req.state.code === null))
      .then(data => {
        console.log(data, 'data')
      this.selectedRequests = data;
    }).catch((data) => {
        this.selectedRequests = data;
        this.messageService.add({
          severity: 'error',
          summary: 'Ошибка регистрации',
          detail: 'Произошла ошибка ',
        })
      });

  }

  formatDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }
}
