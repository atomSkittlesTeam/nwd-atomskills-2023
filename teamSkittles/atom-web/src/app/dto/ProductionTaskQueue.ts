import {StatusRequest} from "./status-request";

export class ProductionTaskQueue {
  id: number;
  productionTaskId: number;
  requestPositionId: number;
  productName: string;

  quantity: number;
  quantityExec: number;
  latheTime: number;

  latheFlag: boolean;

  latheStartTimestamp: Date;
  latheFactTimestamp: Date;

  millingStartTimestamp: Date;
  millingFactTimestamp: Date;
  summaryWorkingTime: Date;
  millingTime: number;
  machineId: number;

  millingFlag: boolean;


  productionPlanStatus: StatusRequest;

  constructor() {
  }
}
