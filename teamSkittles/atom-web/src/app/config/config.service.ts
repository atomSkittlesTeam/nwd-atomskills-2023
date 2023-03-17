import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Config} from "../dto/Config";
import {firstValueFrom} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private configUrl = 'assets/config.json';

  constructor(private httpClient: HttpClient) {
  }

  async getBackendUrl() {
    const config = this.httpClient.get<Config>(this.configUrl);
    return (await firstValueFrom(config)).backendUrl;
  }
}
