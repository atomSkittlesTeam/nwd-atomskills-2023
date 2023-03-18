import {Data} from "@angular/router";

export class ProductionTask {
  id: number;
  productionPlanId: number;
  requestId: number;

  requestNumber: string;
  creationDate: Date;
  closeDate: Date;
  closed: boolean;

  constructor() {
  }
}
