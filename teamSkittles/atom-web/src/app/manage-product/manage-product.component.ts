import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Request} from "../dto/Request";
import {RequestService} from "../services/request.service";
import {MessageService} from "primeng/api";
import {StatusRequest} from "../dto/status-request";

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
      this.countPriority();
    }
  }

  ngOnInit(): void {
    console.log(this.selectedRequests);
  }

  countPriority() {
    this.selectedRequests.forEach((e, idx) => e.priority = idx + 1)
  }

  ngOnDestroy(): void {
    localStorage.removeItem("SendArray");
  }

  onDropChange() {
    this.countPriority();
  }

  async countAutomaticsOrder() {
    this.isManual = false;
    await this.requestService.orderedPlan(this.selectedRequests.map(e => e.id)).catch((data) => {
      this.selectedRequests = data;
      this.messageService.add({
        severity: 'error',
        summary: 'Ошибка регистрации',
        detail: 'Произошла ошибка автоматического расчета',
      })
    });
  }

  async savePlan() {
    await this.requestService.savePlan(this.selectedRequests).catch((data) => {
      this.selectedRequests = data;
      this.messageService.add({
        severity: 'error',
        summary: 'Ошибка регистрации',
        detail: 'Произошла ошибка автоматического расчета',
      })
    });
  }

  async approvePosition(id: number) {

    await this.requestService.approvePosition(id, this.selectedRequests
      .filter(req => req.productionPlanStatus == StatusRequest.BLANK || req.productionPlanStatus == null))
      .catch((data) => {
      this.selectedRequests = data;
      this.messageService.add({
        severity: 'error',
        summary: 'Ошибка регистрации',
        detail: 'Произошла ошибка автоматического расчета',
      })
    });

  }
}
