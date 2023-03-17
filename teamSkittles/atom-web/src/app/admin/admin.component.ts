import {Component, OnInit} from '@angular/core';
import {User} from "../dto/User";
import {MenuItem, MessageService} from "primeng/api";
import {UserService} from "../services/user.service";
import {RolesService} from "../services/roles.service";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
  providers: [MessageService]
})
export class AdminComponent implements OnInit {
  selectedUser: User = new User();
  paymentOptions: any[] = [];
  items: MenuItem[] = [];
  userDto: User = new User();
  user: User[] = [];
  displayDialog: boolean = false;
  roles: { name: string }[] = [];

  constructor(public userService: UserService, public messageService: MessageService, public rolesService: RolesService) {
  }

  async ngOnInit() {
    this.roles = await this.rolesService.getRoles().then(data => data.map(role => { // @ts-ignore
      return {name: role.name.toUpperCase()}}));
    this.items = [
      {label: 'Update', icon: 'pi pi-fw pi-pencil', command: () => this.showDialog()},
      {label: 'Delete', icon: 'pi pi-fw pi-times', command: () => console.log('sss')}
    ];
    this.user = await this.userService.getUsers();

  }

  showDialog() {
    this.displayDialog = true;
    this.userDto = structuredClone(this.selectedUser);
    console.log(this.userDto)
  }

  closeDialog() {
    this.userService.updateUser(this.userDto).then(data => {
      this.selectedUser.role = this.userDto.role;
      this.messageService.add({
        severity: 'success',
        summary: 'Обновился',
        detail: 'Юзер обновлился',
      })
    }).catch(() => this.messageService.add({
      severity: 'error',
      summary: 'Обновления',
      detail: 'Юзер не обновлен',
    }));
    this.displayDialog = false;
  }

}
