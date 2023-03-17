import {Component, OnInit} from '@angular/core';
import {AuthService} from "./services/auth.service";
import {UserService} from "./services/user.service";
import {Router} from "@angular/router";
import {MenuItem} from "primeng/api";
import {Message} from "./dto/Message";
import {RequestService} from "./services/request.service";
import {interval} from "rxjs";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'atomskittles-webapp';
  userAuth: boolean = false;
  userRole: string = '';
  items: MenuItem[] = [];

  messages: Message[] = [];
  display: boolean = false;

  constructor(public authService: AuthService, public router: Router, private userService: UserService, public requestService: RequestService) {
    authService.auth.subscribe(() => this.initUser());
    this.authService.checkAuth();
    this.getMessagesByTime();
  }

  deleteAuthMark() {
    localStorage.removeItem("AUTH");
  }

  public initUser() {
    this.userAuth = this.authService.userAuth;
    this.userRole = this.authService.userRole;
  }

  async showNewPositions() {
    this.messages = await this.requestService.getNewMessages();
    this.display = true;
  }

  getMessagesByTime() {

    interval(60000).subscribe(async () => {
      this.messages = await this.requestService.getNewMessages();

    });
  }

  async ngOnInit(): Promise<void> {
    this.items = [
      {
        icon: 'pi pi-home',
        command: () => {
          this.router.navigate(['']);
        }
      },
      {
        icon: 'pi pi-sign-in',
        command: () => {
          this.authService.logOut();
          this.router.navigate(['/login']);
          // this.messageService.add({ severity: 'success', summary: 'Update', detail: 'Data Updated' });
        }
      },
      {
        icon: 'pi pi-sign-out',
        command: () => {
          this.authService.logOut();
          this.router.navigate(['/registration']);
          // this.messageService.add({ severity: 'error', summary: 'Delete', detail: 'Data Deleted' });
        }
      }
    ];
    if (!this.authService.userAuth) {
      this.router.navigate(['/login']);
    }


  }

  async closeOneInfo(id: number, idx: number) {
    this.messages = this.removeObjectWithId(id);
    await this.requestService.messageSetFrontSing([id])
  }

  async closeAllInfo() {
    let ids = this.messages.map(e => e.id)
    await this.requestService.messageSetFrontSing(ids)
    this.messages = [];
  }

  removeObjectWithId(id: number) {
    const objWithIdIndex = this.messages.findIndex((obj) => obj.id === id);

    if (objWithIdIndex > -1) {
      this.messages.splice(objWithIdIndex, 1);
    }

    return this.messages;
  }
}
