import {Component, OnInit} from '@angular/core';
import {AuthService} from "./services/auth.service";
import {UserService} from "./services/user.service";
import {Router} from "@angular/router";
import {MenuItem} from "primeng/api";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'atomskittles-webapp';
  admin: boolean = false;
  items: MenuItem[] = [];

  constructor(public authService: AuthService, public router: Router, private userService: UserService) {
    this.admin = !!this.authService.get();
  }

  deleteAuthMark() {
    localStorage.removeItem("AUTH");
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
          localStorage.removeItem("AUTH");
          this.router.navigate(['/login']);
          // this.messageService.add({ severity: 'success', summary: 'Update', detail: 'Data Updated' });
        }
      },
      {
        icon: 'pi pi-sign-out',
        command: () => {
          localStorage.removeItem("AUTH");
          this.router.navigate(['/registration']);
          // this.messageService.add({ severity: 'error', summary: 'Delete', detail: 'Data Deleted' });
        }
      }
    ];
    if (!this.authService.get()) {
      this.router.navigate(['/login']);
    }

    if (this.authService.get()) {
      await this.userService.getUserRoles();
    }


  }
}
