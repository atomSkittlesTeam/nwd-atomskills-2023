import { Component } from '@angular/core';
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  login: any = ""
  userAuth: boolean = false;

  constructor(public authService: AuthService,) {
    this.userAuth = this.authService.userAuth;

  }

  deleteAuthMark() {
    localStorage.removeItem("AUTH");
  }

  ngOnInit(): void {
    this.login = localStorage.getItem("LOGIN");
  }
}
