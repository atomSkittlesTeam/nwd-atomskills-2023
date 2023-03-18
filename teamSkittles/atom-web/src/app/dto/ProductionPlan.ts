import {StatusRequest} from "./status-request";

export class ProductionPlan {
  id: number;
  priority: number;
  requestId: number;
  productionPlanStatus: StatusRequest;

  constructor() {
  }
}
