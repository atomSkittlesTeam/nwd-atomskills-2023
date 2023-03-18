import {StatusRequest} from "./status-request";

export class ProductionTaskBatch {
  id: number;
  productionTaskId: number;
  requestPositionId: number;
  productId: number;
  productName: string;

  quantity: number;
  quantityExec: number;
  latheTime: number;
  millingTime: number;
  summaryWorkingTimeBatch: number;

  startBatchTime: Date;

  endBatchTime: Date;

  constructor() {
  }
}
