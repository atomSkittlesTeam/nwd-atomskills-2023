import {EventEmitter, Injectable, Output} from '@angular/core';
import {firstValueFrom} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../dto/User";
import {Router} from "@angular/router";
import {BaseService} from "./base.service";
import {ConfigService} from "../config/config.service";
import {UserService} from "./user.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService extends BaseService {
  userAuth: boolean = false;
  userRole: any;
  @Output() auth = new EventEmitter();

  constructor(private http: HttpClient,
              public router: Router, public override configService: ConfigService, public userService: UserService) {
    super(configService);
  }

  async sendLogin(user: User) {
    const url = await this.getBackendUrl();
    const headers = new HttpHeaders({Authorization: 'Basic ' + window.btoa(user?.login + ':' + user?.password)});
    return await firstValueFrom(this.http.post(url + '/test', {}, {
      headers,
      responseType: 'text' as 'json'
    }))
      .then(async data => {
        localStorage.setItem("AUTH", window.btoa(user?.login + ':' + user?.password));
        localStorage.setItem("LOGIN", <string>user?.login);
        this.userAuth = !!localStorage.getItem("AUTH");
        this.userRole = await this.userService.getUserRoles(user?.login);
        localStorage.setItem("ROLE", this.userRole);
        this.auth.emit();
        await this.router.navigate(['/']);
      });
  }

  checkAuth() {
    if (localStorage.getItem("AUTH")) {
      this.userAuth = true;
      this.userRole = localStorage.getItem("ROLE");
      this.auth.emit();
      
    }
  }

  logOut() {
    localStorage.removeItem("AUTH");
    localStorage.removeItem("LOGIN");
    localStorage.removeItem("ROLE");
    this.userAuth = false;
    this.userRole = null;
    this.auth.emit();
  }

  async registration(login: User | undefined) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.post(url + '/registration', {
      login: login?.login,
      password: login?.password,
      email: login?.mail,
      fullName: login?.fullName
    })).then(data => {
      this.router.navigate(['/login']);
    });
  }
}
