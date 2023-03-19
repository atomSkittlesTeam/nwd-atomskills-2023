import {Component} from '@angular/core';
import {MessageService} from "primeng/api";
import {ProductionTaskBatchItem} from "../../dto/ProductionTaskBatchItem";
import {RequestService} from "../../services/request.service";
import {Request} from "../../dto/Request";
import {formatDate} from "@angular/common";
import {Machine} from "../../dto/Machine";
import {MachineHistory} from "../../dto/MachineHistory";

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss'],
  providers: [MessageService]
})
export class AnalyticsComponent {
  machineList: Machine[] = [];
  selectedMachine: Machine;
  productionTaskBatchItems: ProductionTaskBatchItem[] = [];
  historyList: MachineHistory[] = [];
  display: boolean = false;
  displayStat: boolean = false;
  displayStats: boolean = false;
  dialogHeader: string;
  data: any;
  dataAll: any;
  analyticsMap: Map<string, number>;
  analyticsAllMap: Map<string, number>;

  options = {
    scales: {
      y: {
        title: {
          display: true,
          text: 'Количество произведенных изделий, ШТ'
        }
      },
      x: {
        title: {
          display: true,
          text: 'Код станка'
        }
      }
    }
  }

  constructor(public requestService: RequestService) {
  }

  async ngOnInit() {
    this.machineList = await this.requestService.getAllMachines();
    // this.data = {
    //   labels: ['A','B','C'],
    //   datasets: [
    //     {
    //       data: [300, 50, 100],
    //       backgroundColor: [
    //         "#42A5F5",
    //         "#66BB6A",
    //         "#FFA726"
    //       ],
    //       hoverBackgroundColor: [
    //         "#64B5F6",
    //         "#81C784",
    //         "#FFB74D"
    //       ]
    //     }
    //   ]
    // };
  }

  async showDialog(code: string) {
    this.productionTaskBatchItems = await this.requestService.getAllItemsByMachineCode(code);
    this.display = true;
    this.dialogHeader = code;
  }

  async showDialogStat(code: string, port: number) {
    const almostMap = await this.requestService.getAnalyticsOneMachine(port);
    this.analyticsMap = new Map(Object.entries(almostMap));
    this.data = {
      labels: Array.from(this.analyticsMap.keys()),
      datasets: [
        {
          data: Array.from(this.analyticsMap.values()),
          backgroundColor: [
            "#42A5F5",
            "#66BB6A",
            "#FFA726",
            "#ff2672",
          ],
          hoverBackgroundColor: [
            "#64B5F6",
            "#81C784",
            "#FFB74D",
            "#ff2672"
          ]
        }
      ]
    };
    this.displayStat = true;
    this.dialogHeader = code;
  }

  async showDialogStats() {
    const almostMap = await this.requestService.getAnalyticsAllMachines();
    this.analyticsAllMap = new Map(Object.entries(almostMap));
    this.dataAll = {
      labels: Array.from(this.analyticsAllMap.keys()),
      datasets: [
        {
          label: 'График показывает распределение количества произведенных деталей по каждому станку',
          data: Array.from(this.analyticsAllMap.values()),
          backgroundColor: [
            "#42A5F5",
            "#66BB6A",
            "#FFA726",
            "#ff2672",
          ],
          hoverBackgroundColor: [
            "#64B5F6",
            "#81C784",
            "#FFB74D",
            "#ff2672"
          ]
        }
      ]
    };
    this.displayStats = true;
  }

  formatDate(date: Date) {
    return formatDate(date, 'dd/MM/yyyy', 'en');
  }

  formatInstance(date: Date) {
    return formatDate(Number.parseFloat(date.toString()) * 1000, 'dd/MM/yyyy', 'en');
  }

  isEnabled(request: Request) {
    return (request.state?.code === 'DRAFT' || request.state?.code === 'BLANK' || request.state == null)
  }

  async refreshMachines() {
    this.machineList = await this.requestService.getAllMachines();
  }

}
