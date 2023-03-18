import {Component, OnInit} from '@angular/core';
import {AuthService} from "./services/auth.service";
import {UserService} from "./services/user.service";
import {Router} from "@angular/router";
import {ConfirmationService, MenuItem, MessageService} from "primeng/api";
import {Message} from "./dto/Message";
import {RequestService} from "./services/request.service";
import {interval} from "rxjs";
import {Enums} from "./dto/enums";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [ConfirmationService, MessageService]
})
export class AppComponent implements OnInit {
  title = 'atomskittles-webapp';
  userAuth: boolean = false;
  userRole: string = '';
  userLogin: string | null = '';
  items: MenuItem[] = [];

  enums = Enums;


  messages: Message[] = [];
  display: boolean = false;
  Enums = Enums;
  isDialogShown: boolean = false;

  constructor(public authService: AuthService,
              public router: Router,
              private userService:
                UserService, public requestService:
                RequestService,
              private confirmationService: ConfirmationService,
              public messageService: MessageService) {
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
    this.userLogin = localStorage.getItem("LOGIN");
  }

  async showNewPositions() {
    this.display = true;
    this.messages = await this.requestService.getNewMessages();

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
    this.messages = await this.requestService.getNewMessages();
    this.createBrokenArray();
  }

  createBrokenArray() {
    let brokenMachine: Message[] = [];
    this.messages.forEach(mes => {
      if (mes.type === Enums.machineBroke) {
        brokenMachine.push(mes);
      }
    })

    localStorage.setItem("MACHINES_BROKEN", JSON.stringify(brokenMachine))
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

  confirm(message: Message, indexMesage: number) {
    this.confirmationService.confirm({
      message: `Вы уверены что хотите отправить станок ${message.objectName} в ремонт?`,
      accept: async () => {
        await this.requestService.sendMachineToRepairing(message.objectName).then(data => {
          this.closeOneInfo(message.id, indexMesage);
          this.messageService.add({
            severity: 'success',
            summary: 'Обновился',
            detail: 'Отправка прошла успешна',
          })
        }).catch((e) => {
          console.log(e, 'error')
          this.messageService.add({
            severity: 'error',
            summary: 'Ошибка при отправке',
            detail: e.error?.message
          })
        });
      }
    });
  }
}
