import { Injectable } from '@angular/core';
import {ConfigService} from "../config/config.service";


@Injectable({
  providedIn: 'root'
})
export class BaseService {

  constructor(public configService: ConfigService) { }

  async getBackendUrl() {
    return await this.configService.getBackendUrl();
  }
}
