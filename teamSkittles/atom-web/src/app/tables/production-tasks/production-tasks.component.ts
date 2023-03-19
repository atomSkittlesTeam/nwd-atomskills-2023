import {Component, OnInit} from '@angular/core';
import {formatDate} from "@angular/common";
import {Request} from "../../dto/Request";
import {RequestService} from "../../services/request.service";
import {MessageService} from "primeng/api";
import {ProductionTask} from "../../dto/ProductionTask";
import {ProductionTaskBatch} from "../../dto/ProductionTaskBatch";
import {ProductionTaskBatchItem} from "../../dto/ProductionTaskBatchItem";
import {Data} from "@angular/router";

@Component({
  selector: 'app-production-tasks',
  templateUrl: './production-tasks.component.html',
  styleUrls: ['./production-tasks.component.scss'],
  providers: [MessageService]
})
export class ProductionTasksComponent implements OnInit {

  productionTask: ProductionTask[] = [];
  selectedProductionTask: ProductionTask;

  productionTaskBatch: ProductionTaskBatch[] = [];

  //первый детейл
  selectedProductionTaskBatch: ProductionTaskBatch;

  //Второй детейл
  productionTaskBatchItem: ProductionTaskBatchItem[] = [];
  selectedProductionTaskBatchItem: ProductionTaskBatchItem;
  display: any;
  checked: boolean = false;
  dialogHeader: string;
  displayModal: boolean;
  request: Request;

  constructor(public requestService: RequestService) {
  }

  async ngOnInit() {
    this.productionTask = await this.requestService.getAllTasks();
    console.log()
  }

  async showDialog(batch: ProductionTaskBatch) {
    this.display = true;
    this.dialogHeader = batch?.id.toString();
    this.productionTaskBatchItem = await this.requestService.getAllBatchItemsByBatch(batch.id);
  }

  formatDate(date: Data | string) {
    // @ts-ignore
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }

  isEnabled(request: Request) {
    return (request.state?.code === 'DRAFT' || request.state?.code === 'BLANK' || request.state == null)
  }

  async checkRequest(selectedProductionTask: ProductionTask) {
    this.selectedProductionTask = selectedProductionTask;
    this.productionTaskBatch = await this.requestService.getAllBatchesByTask(selectedProductionTask.id);

  }

  async refreshMain() {
    this.productionTask = await this.requestService.getAllTasks();
  }

  async refreshDitail() {
    this.productionTaskBatch = await this.requestService.getAllBatchesByTask(this.selectedProductionTask.id);

  }

  async recountPosition(req: any) {
    this.displayModal = true;
    let arrayRequests = await this.requestService.getRequests();
    // @ts-ignore
    this.request = arrayRequests.find(e => e.number === req.requestNumber);
    console.log(this.request)
  }
}

