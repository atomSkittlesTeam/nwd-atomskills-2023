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
  userRole: string = '';

  constructor(public authService: AuthService,) {
    this.userAuth = this.authService.userAuth;
    this.userRole = this.authService.userRole;

  }

  deleteAuthMark() {
    localStorage.removeItem("AUTH");
  }

  ngOnInit(): void {
    this.login = localStorage.getItem("LOGIN");
  }
}
