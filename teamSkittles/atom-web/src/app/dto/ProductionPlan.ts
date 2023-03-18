import {StatusRequest} from "./status-request";
import {Constractor} from "./Constractor";
import {State} from "./State";

export class ProductionPlan {
  id: number;
  requestId: number;
  number: number;
  date: Date;

  contractor: Constractor;
  description: string;
  state: State;
  releaseDate: Date;

  time: number;

  priority: number;
  productionPlanStatus: StatusRequest;


  constructor() {
  }
}
