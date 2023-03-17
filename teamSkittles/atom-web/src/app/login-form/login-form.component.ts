import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {User} from "../dto/User";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
  providers: [MessageService]
})
export class LoginFormComponent implements OnInit {
  user: any = new User();
  isRegistrationForm: boolean = false;
  title: string = "";
  buttonLabel: string = "";

  constructor(public authService: AuthService, public router: Router, public messageService: MessageService) {
  }

  async sendRequest() {
    this.isRegistrationForm ? await this.authService.registration(this.user).catch(() => this.messageService.add({
      severity: 'error',
      summary: 'Ошибка регистрации',
      detail: 'Произошла ошибка Регистрации',
    })) : await this.authService.sendLogin(this.user).catch(() => this.messageService.add({
      severity: 'error',
      summary: 'Ошибка авторизации!',
      detail: 'Произошла ошибка Авторизации, попробуйте еще раз',
      sticky: true
    }));
    return 0;
  }

  ngOnInit(): void {
    this.isRegistrationForm = this.router.url == "/registration";
    this.changeForm();
  }

  changeForm() {
    this.title = this.isRegistrationForm ? 'Регистрация' : "Войти";
    this.buttonLabel = this.isRegistrationForm ? 'Зарегистрироваться' : "Войти";
  }
}
