import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user/user.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './_guards/auth.guard';
import {RegisterComponent} from './register/register.component';
import {ShopComponent} from './shop/shop.component';
import {InvoicesComponent} from './invoices/invoices.component';
import {ArchivesComponent} from './archives/archives.component';

const routes: Routes = [
  {
    path: 'user',
    component: UserComponent,
    canActivate: [AuthGuard] // To enforce authentication
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'shop',
    component: ShopComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'invoices',
    component: InvoicesComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'archives',
    component: ArchivesComponent,
    canActivate: [AuthGuard]
  }
];

@NgModule({
  exports: [RouterModule],
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })]
})
export class AppRoutingModule {}
