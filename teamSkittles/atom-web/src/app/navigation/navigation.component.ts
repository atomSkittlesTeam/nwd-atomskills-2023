import { Component } from '@angular/core';
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  login: any = ""
  admin: boolean = false;

  constructor(public authService: AuthService,) {
    this.admin = !!this.authService.get();

  }

  deleteAuthMark() {
    localStorage.removeItem("AUTH");
  }

  ngOnInit(): void {
    this.login = localStorage.getItem("LOGIN");
  }
}
