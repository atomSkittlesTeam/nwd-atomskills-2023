import {Component, OnInit} from '@angular/core';
import {formatDate} from "@angular/common";
import {Request} from "../../dto/Request";
import {RequestService} from "../../services/request.service";
import {MessageService} from "primeng/api";
import {ProductionTask} from "../../dto/ProductionTask";

@Component({
  selector: 'app-production-tasks',
  templateUrl: './production-tasks.component.html',
  styleUrls: ['./production-tasks.component.scss'],
  providers: [MessageService]
})
export class ProductionTasksComponent implements OnInit {

  productionTask: ProductionTask[] = [];
  selectedProductionTask: ProductionTask[] = [];
  display: any;
  checked: boolean = false;

  constructor(public requestService: RequestService) {
  }

  async ngOnInit() {
    this.productionTask = await this.requestService.getAllTasks();
  }

  showDialog() {
    this.display = true;
    console.log(this.selectedProductionTask)
  }

  formatDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }

  isEnabled(request: Request) {
    return (request.state?.code === 'DRAFT' || request.state?.code === 'BLANK' || request.state == null)
  }

}

