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
}
