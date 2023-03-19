import {State} from "./State";

export class Machine {
  id: number;
  code: string;
  state: State;
  advInfo: any;
  beginDateTime: Date;
  endDateTime: Date;
  machineType: any;
  port: number;
}
