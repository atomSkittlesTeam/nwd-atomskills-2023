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
import { RequestComponent } from './tables/request/request.component';
import {SpeedDialModule} from "primeng/speeddial";
import {SplitterModule} from "primeng/splitter";
import {SidebarModule} from 'primeng/sidebar'
import {ToolbarModule} from "primeng/toolbar";
import { ManageProductComponent } from './manage-product/manage-product.component';
import {InputSwitchModule} from "primeng/inputswitch";
import {BlockUIModule} from "primeng/blockui";
import { ProductionTasksComponent } from './tables/production-tasks/production-tasks.component';
import {CheckboxModule} from "primeng/checkbox";
import {ConfirmDialogModule} from "primeng/confirmdialog";
@NgModule({
  declarations: [
    AppComponent,
    LoginFormComponent,
    NavigationComponent,
    AdminComponent,
    RequestComponent,
    ManageProductComponent,
    ProductionTasksComponent
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
    SpeedDialModule,
    SplitterModule,
    SidebarModule,
    ToolbarModule,
    InputSwitchModule,
    BlockUIModule,
    CheckboxModule,
    ConfirmDialogModule
  ],
  providers: [ConfigService, {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthHelperInterceptor,
    multi: true,
  },],
  bootstrap: [AppComponent]
})
export class AppModule { }
