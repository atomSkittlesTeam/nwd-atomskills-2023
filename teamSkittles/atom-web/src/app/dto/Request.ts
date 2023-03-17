import {Data} from "@angular/router";
import {State} from "./State";
import {Constractor} from "./Constractor";

export class Request {
  id: number;
  number: string;
  date: Data;
  contractor: Constractor;
  description: string;
  state: State;
  releaseDate: Data;

  constructor() {
  }
}
