import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginFormComponent} from "./login-form/login-form.component";
import {NavigationComponent} from "./navigation/navigation.component";
import {AdminComponent} from "./admin/admin.component";

const routes: Routes = [
  {path: '', component: NavigationComponent},
  {path: 'login', component: LoginFormComponent},
  {path: 'registration', component: LoginFormComponent},
  // {path: 'parsing', component: ParsingComponent},
  {path: 'admin', component: AdminComponent},
  // {path: 'process', component: ProcessPageComponent},
  // {path: 'stat', component: StatComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
