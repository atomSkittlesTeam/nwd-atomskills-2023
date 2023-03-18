import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginFormComponent} from "./login-form/login-form.component";
import {NavigationComponent} from "./navigation/navigation.component";
import {AdminComponent} from "./admin/admin.component";
import {RequestComponent} from "./tables/request/request.component";
import {ManageProductComponent} from "./manage-product/manage-product.component";
import {ProductionTasksComponent} from "./tables/production-tasks/production-tasks.component";

const routes: Routes = [
  {path: '', component: NavigationComponent},
  {path: 'login', component: LoginFormComponent},
  {path: 'registration', component: LoginFormComponent},
  {path: 'admin', component: AdminComponent},
  {path: 'request', component: RequestComponent},
  {path: 'manage', component: ManageProductComponent},
  {path: 'production-tasks', component: ProductionTasksComponent},
  // {path: 'stat', component: StatComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
