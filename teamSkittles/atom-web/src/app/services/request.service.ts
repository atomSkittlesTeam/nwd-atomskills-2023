import {Injectable} from '@angular/core';
import {firstValueFrom} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {ConfigService} from "../config/config.service";
import {BaseService} from "./base.service";
import {User} from "../dto/User";
import {Request} from "../dto/Request";
import {RequestPosition} from "../dto/RequestPosition";
import {Message} from "../dto/Message";
import {ProductionTask} from "../dto/ProductionTask";
import {ProductionTaskBatch} from "../dto/ProductionTaskBatch";
import {ProductionTaskBatchItem} from "../dto/ProductionTaskBatchItem";
import {Machine} from "../dto/Machine";
import {MachineHistory} from "../dto/MachineHistory";

@Injectable({
  providedIn: 'root'
})
export class RequestService extends BaseService {

  constructor(private http: HttpClient,
              public router: Router,
              public override configService: ConfigService) {
    super(configService);
  }

  async getRequests() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Request[]>(url + '/requests'));
  }

  async getRequestPositionById(id: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<RequestPosition[]>(url + `/requests/${id}/items`));
  }

  async getNewMessages() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Message[]>(url + `/requests/new-messages`));
  }

  async messageSetFrontSing(ids: number[]) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post(url + `/requests/message`, ids));
  }

  async orderedPlan(ids: number[]) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post<any[]>(url + `/requests/ordered-plan`, ids));
  }

  async savePlan(listRequests: any[]) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post<Request[]>(url + `/requests/save-blank`, listRequests));
  }

  async approvePosition(id: number, listRequests: any[]) {
    console.log(id, 'id');
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post<Request[]>(url + `/requests/approve-plan/${id}`, listRequests));
  }

  async getBlank() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Request[]>(url + `/requests/get-plan`));
  }

  async approveProductionPlan(id: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post<Request[]>(url + `/production/plan/${id}/task`, {}));
  }

  async getProductionTask() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<ProductionTask[]>(url + `/production/plan/tasks`, {}));
  }

  async approveProductionPlan2(id: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post<Request[]>(url + `/production/plan/tasks/${id}`, {}));
  }

  async sendMachineToRepairing(machineCode: string) {
    console.log(machineCode, 'ssa')
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Request[]>(url + `/machine/get-broken-machine-by-id/${machineCode}`, {}));
  }

  async getAllTasks() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<ProductionTask[]>(url + `/production/all-tasks`, {}));
  }

  async getAllBatchesByTask(id: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<ProductionTaskBatch[]>(url + `/production/all-batches/${id}`, {}));
  }

  async getAllBatchItemsByBatch(id: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<ProductionTaskBatchItem[]>(url + `/production/all-batch-items/${id}`, {}));
  }

  async getAllMachines() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Machine[]>(url + `/production/all-machines`, {}));
  }

  async getAllItemsByMachineCode(machineCode: string) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<ProductionTaskBatchItem[]>(url + `/production/all-batch-items-by-machine/${machineCode}`, {}));
  }

  async getHistoryOfMachineByPort(machinePort: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<MachineHistory[]>(url + `/production/history/${machinePort}`, {}));
  }

  async getAnalyticsOneMachine(machinePort: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Map<string, number>>(url + `/production/analytics/${machinePort}`, {}));
  }

  async getAnalyticsAllMachines() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Map<string, number>>(url + `/production/analytics-all`, {}));
  }
}
