import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Request} from "../dto/Request";
import {RequestService} from "../services/request.service";
import {MessageService} from "primeng/api";
import {StatusRequest} from "../dto/status-request";
import {formatDate} from "@angular/common";
import {Enums} from "../dto/enums";

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
  blocked: any = false;

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
      .filter(req => req.state?.code === "BLANK" || req.state?.code === "DRAFT" || req.state === null)
      .map(e => e.requestId)).then(data => {
      console.log(data, 'autoOrder');
      this.selectedRequests = data;
      this.messageService.add({
        severity: 'success',
        summary: 'Пересчет произошел',
      })
    }).catch((data) => {
      this.messageService.add({
        severity: 'error',
        summary: 'Ошибка регистрации',
        detail: 'Произошла ошибка',
      })
    });
  }

  async savePlan() {
    await this.requestService.savePlan(this.selectedRequests
      .filter(req => req.state?.code === "BLANK" || req.state?.code === "DRAFT" || req.state === null))
      .then(data => {
        this.selectedRequests = data;
        this.messageService.add({
          severity: 'success',
          summary: 'План сохранился',
        })
      }).catch((data) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Ошибка сохранения плана',
          detail: 'Произошла ошибка',
        })
      });
  }

  async approvePosition(id: number) {
    this.blocked = true;
    await this.requestService.approvePosition(id, this.selectedRequests
      .filter(req => req.state?.code === "BLANK" || req.state?.code === "DRAFT" || req.state === null))
      .then(data => {
        this.selectedRequests = data;
        this.messageService.add({
          severity: 'success',
          summary: 'Утверждение прошло успешно',
        })
        this.blocked = false;

      }).catch((data) => {
        this.selectedRequests = data;
        this.messageService.add({
          severity: 'error',
          summary: 'Ошибка утверждения',
          detail: 'Произошла ошибка ',
        });
        this.blocked = false;

      });

  }

  formatDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }

  async toProduction(request: Request) {
    console.log(request, 'req')
    this.blocked = true;
    await this.requestService.approveProductionPlan(request.id).then(data => {
      this.messageService.add({
        severity: 'success',
        summary: 'Выполнено',
        detail: 'Заказ-наряд сформирован'
      })
      request.state.code = StatusRequest.IN_PRODUCTION;
      this.blocked = false;
    }).catch((data) => {
      this.selectedRequests = data;
      this.messageService.add({
        severity: 'error',
        summary: 'Ошибка формирования заказ-наряда',
        detail: 'Произошла ошибка ',
      });
      this.blocked = false;
    });
  }
}
