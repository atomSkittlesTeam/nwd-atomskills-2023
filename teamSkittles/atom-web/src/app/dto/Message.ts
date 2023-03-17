import {Enums} from "./enums";
import {Data} from "@angular/router";

export class Message {
  id: number;
  type: Enums;
  customText: string;
  emailSign: boolean;
  frontSign: boolean;

  objectId: number;
  objectName: string;
  instant: Data;

  constructor() {
  }
}
