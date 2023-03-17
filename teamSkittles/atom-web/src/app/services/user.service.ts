import {Injectable} from '@angular/core';
import {firstValueFrom} from "rxjs";
import {User} from "../dto/User";
import {ConfigService} from '../config/config.service';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {BaseService} from "./base.service";

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseService {
  constructor(private http: HttpClient,
              public router: Router,
              public override configService: ConfigService) {
    super(configService);
  }

  async getUsers() {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.get<User[]>(url + '/user/all'));
  }

  async updateUser(userDto: User) {
    const url = await this.getBackendUrl();
    return await firstValueFrom(this.http.put(url + '/user/update', userDto));
  }

  async getUserRoles() {
    let login = localStorage.getItem("LOGIN");
    const url = await this.getBackendUrl();
    console.log(login, "data")

    return await firstValueFrom(this.http.get<any>(url + `/user/${login}/roles`)
    ).then(data => {
      console.log(data)
      localStorage.setItem("ROLES", data)
    });
  }
}
