import { Injectable } from '@angular/core';
import {BaseService} from "./base.service";
import {HttpClient} from "@angular/common/http";
import {Role} from "../dto/Role";
import {firstValueFrom} from "rxjs";
import {ConfigService} from "../config/config.service";

@Injectable({
  providedIn: 'root'
})
export class RolesService extends BaseService {

  constructor(private http: HttpClient,
              public override configService: ConfigService) {
    super(configService);
  }

  async getDataById(id: number) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Role>(url + "/role/" + `get-data/id/${id}`));
  }

  async getDataByName(name: string) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Role>(url + "/role/" + `get-data/name/${name}`));
  }

  async getRoles() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Role[]>(url + "/role/" + 'get-data'));
  }

  async getDataAsMapIds() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Map<Number, Role>>(url + "/role/" + 'get-data-as-map/id'));
  }

  async getDataAsMapNames() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<Map<Number, Role>>(url + "/role/" + 'get-data-as-map/name'));
  }

}
