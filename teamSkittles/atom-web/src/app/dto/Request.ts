import {Data} from "@angular/router";
import {State} from "./State";
import {Constractor} from "./Constractor";
import {StatusRequest} from "./status-request";

export class Request {
  id: number;
  requestId: number;
  number: string;
  date: Data;
  contractor: Constractor;
  priority: number = 0;
  description: string;
  state: State;
  time: number;
  productionPlanStatus: StatusRequest = StatusRequest.BLANK;
  releaseDate: Data;

  constructor() {
  }
}
