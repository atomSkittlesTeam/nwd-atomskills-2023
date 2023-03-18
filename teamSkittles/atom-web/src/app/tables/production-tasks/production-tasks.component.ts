import {Component, OnInit} from '@angular/core';
import {formatDate} from "@angular/common";
import {Request} from "../../dto/Request";
import {RequestService} from "../../services/request.service";
import {MessageService} from "primeng/api";
import {ProductionTask} from "../../dto/ProductionTask";
import {ProductionTaskBatch} from "../../dto/ProductionTaskBatch";
import {ProductionTaskBatchItem} from "../../dto/ProductionTaskBatchItem";

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

  constructor(public requestService: RequestService) {
  }

  async ngOnInit() {
    this.productionTask = await this.requestService.getAllTasks();
    console.log()
  }

  async showDialog(batch: ProductionTaskBatch) {
    this.display = true;
    this.productionTaskBatchItem = await this.requestService.getAllBatchItemsByBatch(batch.id);
  }

  formatDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }

  isEnabled(request: Request) {
    return (request.state?.code === 'DRAFT' || request.state?.code === 'BLANK' || request.state == null)
  }

  async checkRequest(selectedProductionTask: ProductionTask) {
    console.log(selectedProductionTask)
    this.productionTaskBatch = await this.requestService.getAllBatchesByTask(selectedProductionTask.id);
    console.log(this.productionTaskBatch);

  }
}

