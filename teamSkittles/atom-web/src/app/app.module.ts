import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginFormComponent } from './login-form/login-form.component';
import {MessageModule} from "primeng/message";
import {MessagesModule} from "primeng/messages";
import {FormsModule} from "@angular/forms";
import {ButtonModule} from "primeng/button";
import {TableModule} from "primeng/table";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { NavigationComponent } from './navigation/navigation.component';
import { AdminComponent } from './admin/admin.component';
import {ToastModule} from "primeng/toast";
import {DialogModule} from "primeng/dialog";
import {SelectButtonModule} from "primeng/selectbutton";
import {ContextMenuModule} from "primeng/contextmenu";
import {ConfigService} from "./config/config.service";
import {AuthHelperInterceptor} from "./config/auth-helper.interceptor";
import {MultiSelectModule} from "primeng/multiselect";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent,
    LoginFormComponent,
    NavigationComponent,
    AdminComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    MessageModule,
    MessagesModule,
    FormsModule,
    ButtonModule,
    TableModule,
    ToastModule,
    DialogModule,
    SelectButtonModule,
    ContextMenuModule,
    BrowserAnimationsModule,
    MultiSelectModule,

  ],
  providers: [ConfigService, {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthHelperInterceptor,
    multi: true,
  },],
  bootstrap: [AppComponent]
})
export class AppModule { }
