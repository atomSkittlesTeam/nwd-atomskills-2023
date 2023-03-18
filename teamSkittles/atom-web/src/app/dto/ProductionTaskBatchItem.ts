export class ProductionTaskBatchItem {
  id: number;
  batchId: number;
  latheStartTimestamp: Date;
  latheFinishedTimestamp: Date;
  latheFactTime: Date;
  millingStartTimestamp: Date;
  millingFinishedTimestamp: Date;
  millingFactTime: Date;
  latheMachineCode: string;

  millingMachineCode: string;

  summaryWorkingTimeProduct: number;

  constructor() {
  }
}
