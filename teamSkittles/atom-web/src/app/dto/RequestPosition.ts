import {Product} from "./Product";
import {Request} from "./Request";

export class RequestPosition {
  id: number;
  request: Request;
  product: Product;
  quantity: number;
  quantityExec: number;
  constructor() {
  }
}
