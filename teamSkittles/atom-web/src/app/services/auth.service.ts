import {Injectable} from '@angular/core';
import {firstValueFrom} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../dto/User";
import {Router} from "@angular/router";
import {BaseService} from "./base.service";
import {ConfigService} from "../config/config.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService extends BaseService {

  constructor(private http: HttpClient,
              public router: Router, public override configService: ConfigService) {
    super(configService);
  }

  async sendLogin(login: User | undefined) {
    const url = await this.getBackendUrl();
    const headers = new HttpHeaders({Authorization: 'Basic ' + window.btoa(login?.login + ':' + login?.password)});
    return await firstValueFrom(this.http.post(url + '/test', {}, {
      headers,
      responseType: 'text' as 'json'
    })).then(data => {
      localStorage.setItem("AUTH", window.btoa(login?.login + ':' + login?.password));
      localStorage.setItem("LOGIN", <string>login?.login);
      this.router.navigate(['/']);
    });
  }

  async registration(login: User | undefined) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post(url + '/registration', {
      login: login?.login,
      password: login?.password,
      mail: login?.mail,
      fullname: login?.fullName
    })).then(data => {
      this.router.navigate(['/login']);
    });
  }

  get() {
    return localStorage.getItem("AUTH")
  }
}
